package com.food.ordering.system.foodOrderingSystem.service;

import com.food.ordering.system.foodOrderingSystem.dto.OrderItemRequest;
import com.food.ordering.system.foodOrderingSystem.dto.OrderRequest;
import com.food.ordering.system.foodOrderingSystem.dto.OrderResponse;
import com.food.ordering.system.foodOrderingSystem.entity.FoodItem;
import com.food.ordering.system.foodOrderingSystem.entity.Order;
import com.food.ordering.system.foodOrderingSystem.entity.OrderItem;
import com.food.ordering.system.foodOrderingSystem.entity.OrderStatus;
import com.food.ordering.system.foodOrderingSystem.entity.Role;
import com.food.ordering.system.foodOrderingSystem.entity.User;
import com.food.ordering.system.foodOrderingSystem.repository.FoodItemRepository;
import com.food.ordering.system.foodOrderingSystem.repository.OrderItemRepository;
import com.food.ordering.system.foodOrderingSystem.repository.OrderRepository;
import com.food.ordering.system.foodOrderingSystem.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private FoodItemRepository foodItemRepository;
    @Mock private OrderRepository orderRepository;
    @Mock private OrderItemRepository orderItemRepository;

    @InjectMocks
    private OrderService orderService;

    private User aliceUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("alice@example.com");
        user.setName("Alice");
        user.setRole(Role.USER);
        return user;
    }

    @Test
    void placeOrderComputesTotalAndPersistsItems() {
        User user = aliceUser();
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));

        FoodItem pizza = new FoodItem();
        pizza.setId(10L);
        pizza.setPrice(200.0);
        FoodItem burger = new FoodItem();
        burger.setId(11L);
        burger.setPrice(150.0);

        when(foodItemRepository.findById(10L)).thenReturn(Optional.of(pizza));
        when(foodItemRepository.findById(11L)).thenReturn(Optional.of(burger));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> {
            Order o = inv.getArgument(0);
            if (o.getId() == null) o.setId(99L);
            return o;
        });

        OrderItemRequest item1 = new OrderItemRequest();
        item1.setFoodId(10L);
        item1.setQuantity(2);
        OrderItemRequest item2 = new OrderItemRequest();
        item2.setFoodId(11L);
        item2.setQuantity(1);

        OrderRequest req = new OrderRequest();
        req.setItems(List.of(item1, item2));
        req.setDeliveryAddress("221B Baker St");
        req.setPhone("9999999999");

        Order saved = orderService.placeOrderByEmail("alice@example.com", req);

        assertThat(saved.getTotalAmount()).isEqualTo(550.0);
        assertThat(saved.getStatus()).isEqualTo(OrderStatus.PLACED);
        assertThat(saved.getDeliveryAddress()).isEqualTo("221B Baker St");
        assertThat(saved.getPhone()).isEqualTo("9999999999");
        assertThat(saved.getUser()).isSameAs(user);
        verify(orderItemRepository, times(2)).save(any(OrderItem.class));
    }

    @Test
    void placeOrderFailsForEmptyItems() {
        OrderRequest req = new OrderRequest();
        req.setItems(List.of());
        assertThatThrownBy(() -> orderService.placeOrderByEmail("alice@example.com", req))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void placeOrderFailsForUnknownUser() {
        when(userRepository.findByEmail("ghost@example.com")).thenReturn(Optional.empty());
        OrderItemRequest one = new OrderItemRequest();
        one.setFoodId(1L);
        one.setQuantity(1);
        OrderRequest req = new OrderRequest();
        req.setItems(List.of(one));

        assertThatThrownBy(() -> orderService.placeOrderByEmail("ghost@example.com", req))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void placeOrderFailsWhenFoodMissing() {
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(aliceUser()));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));
        when(foodItemRepository.findById(999L)).thenReturn(Optional.empty());

        OrderItemRequest bad = new OrderItemRequest();
        bad.setFoodId(999L);
        bad.setQuantity(1);
        OrderRequest req = new OrderRequest();
        req.setItems(List.of(bad));

        assertThatThrownBy(() -> orderService.placeOrderByEmail("alice@example.com", req))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void cancelOrderSucceedsWhenPlaced() {
        User user = aliceUser();
        Order order = new Order();
        order.setId(50L);
        order.setUser(user);
        order.setStatus(OrderStatus.PLACED);

        when(orderRepository.findById(50L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));
        when(orderItemRepository.findByOrderId(50L)).thenReturn(List.of());

        OrderResponse res = orderService.cancelOrder(50L, "alice@example.com");

        assertThat(res.getStatus()).isEqualTo("CANCELLED");
    }

    @Test
    void cancelOrderRejectedAfterConfirmed() {
        User user = aliceUser();
        Order order = new Order();
        order.setId(50L);
        order.setUser(user);
        order.setStatus(OrderStatus.CONFIRMED);

        when(orderRepository.findById(50L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.cancelOrder(50L, "alice@example.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("PLACED");
    }

    @Test
    void cancelOrderRejectsOtherUser() {
        User user = aliceUser();
        Order order = new Order();
        order.setId(50L);
        order.setUser(user);
        order.setStatus(OrderStatus.PLACED);

        when(orderRepository.findById(50L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.cancelOrder(50L, "stranger@example.com"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void updateStatusAcceptsValidEnum() {
        User user = aliceUser();
        Order order = new Order();
        order.setId(60L);
        order.setUser(user);
        order.setStatus(OrderStatus.PLACED);

        when(orderRepository.findById(60L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));
        when(orderItemRepository.findByOrderId(60L)).thenReturn(List.of());

        OrderResponse res = orderService.updateStatus(60L, "PREPARING");

        assertThat(res.getStatus()).isEqualTo("PREPARING");
    }

    @Test
    void updateStatusRejectsInvalidEnum() {
        Order order = new Order();
        order.setId(60L);
        order.setUser(aliceUser());
        order.setStatus(OrderStatus.PLACED);

        when(orderRepository.findById(60L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.updateStatus(60L, "INVENTED"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getOrderDetailDeniesForOtherUser() {
        Order order = new Order();
        order.setId(70L);
        order.setUser(aliceUser());
        order.setStatus(OrderStatus.PLACED);

        when(orderRepository.findById(70L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.getOrderDetail(70L, "stranger@example.com", false))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getOrderDetailAllowsAdminForAnyOrder() {
        Order order = new Order();
        order.setId(70L);
        order.setUser(aliceUser());
        order.setStatus(OrderStatus.PLACED);

        when(orderRepository.findById(70L)).thenReturn(Optional.of(order));
        when(orderItemRepository.findByOrderId(70L)).thenReturn(List.of());

        OrderResponse res = orderService.getOrderDetail(70L, "admin@example.com", true);
        assertThat(res.getId()).isEqualTo(70L);
    }
}
