package com.food.ordering.system.foodOrderingSystem.service;

import com.food.ordering.system.foodOrderingSystem.dto.OrderItemRequest;
import com.food.ordering.system.foodOrderingSystem.dto.OrderItemResponse;
import com.food.ordering.system.foodOrderingSystem.dto.OrderRequest;
import com.food.ordering.system.foodOrderingSystem.dto.OrderResponse;
import com.food.ordering.system.foodOrderingSystem.entity.FoodItem;
import com.food.ordering.system.foodOrderingSystem.entity.Order;
import com.food.ordering.system.foodOrderingSystem.entity.OrderItem;
import com.food.ordering.system.foodOrderingSystem.entity.OrderStatus;
import com.food.ordering.system.foodOrderingSystem.entity.User;
import com.food.ordering.system.foodOrderingSystem.repository.FoodItemRepository;
import com.food.ordering.system.foodOrderingSystem.repository.OrderItemRepository;
import com.food.ordering.system.foodOrderingSystem.repository.OrderRepository;
import com.food.ordering.system.foodOrderingSystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FoodItemRepository foodItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    public Order placeOrderByEmail(String email, OrderRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PLACED);
        order.setCreatedAt(LocalDateTime.now());
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setPhone(request.getPhone());

        Order savedOrder = orderRepository.save(order);

        double total = 0;
        for (OrderItemRequest itemReq : request.getItems()) {
            FoodItem food = foodItemRepository.findById(itemReq.getFoodId())
                    .orElseThrow(() -> new IllegalArgumentException("Food not found: " + itemReq.getFoodId()));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setFoodItem(food);
            orderItem.setQuantity(itemReq.getQuantity());

            total += food.getPrice() * itemReq.getQuantity();
            orderItemRepository.save(orderItem);
        }

        savedOrder.setTotalAmount(total);
        return orderRepository.save(savedOrder);
    }

    public List<OrderResponse> getMyOrders(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public OrderResponse getOrderDetail(Long id, String requesterEmail, boolean isAdmin) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        if (!isAdmin && !order.getUser().getEmail().equals(requesterEmail)) {
            throw new IllegalArgumentException("Cannot view another user's order");
        }
        return toResponse(order);
    }

    public OrderResponse updateStatus(Long id, String newStatus) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        OrderStatus parsed;
        try {
            parsed = OrderStatus.valueOf(newStatus);
        } catch (IllegalArgumentException | NullPointerException ex) {
            throw new IllegalArgumentException("Invalid status: " + newStatus);
        }
        order.setStatus(parsed);
        return toResponse(orderRepository.save(order));
    }

    public OrderResponse cancelOrder(Long id, String requesterEmail) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        if (!order.getUser().getEmail().equals(requesterEmail)) {
            throw new IllegalArgumentException("Cannot cancel another user's order");
        }
        if (order.getStatus() != OrderStatus.PLACED) {
            throw new IllegalArgumentException("Order can only be cancelled while in PLACED status");
        }
        order.setStatus(OrderStatus.CANCELLED);
        return toResponse(orderRepository.save(order));
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        List<OrderItemResponse> itemDtos = items.stream()
                .map(i -> new OrderItemResponse(
                        i.getId(),
                        i.getFoodItem() != null ? i.getFoodItem().getId() : null,
                        i.getFoodItem() != null ? i.getFoodItem().getName() : null,
                        i.getFoodItem() != null ? i.getFoodItem().getPrice() : null,
                        i.getQuantity(),
                        i.getFoodItem() != null
                                ? i.getFoodItem().getPrice() * i.getQuantity()
                                : 0.0
                ))
                .collect(Collectors.toList());

        return new OrderResponse(
                order.getId(),
                order.getTotalAmount(),
                order.getStatus() != null ? order.getStatus().name() : null,
                order.getDeliveryAddress(),
                order.getPhone(),
                order.getCreatedAt(),
                order.getUser() != null ? order.getUser().getEmail() : null,
                order.getUser() != null ? order.getUser().getName() : null,
                itemDtos
        );
    }
}
