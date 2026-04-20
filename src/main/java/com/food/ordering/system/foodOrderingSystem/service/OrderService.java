package com.food.ordering.system.foodOrderingSystem.service;


import com.food.ordering.system.foodOrderingSystem.dto.OrderItemRequest;
import com.food.ordering.system.foodOrderingSystem.dto.OrderRequest;
import com.food.ordering.system.foodOrderingSystem.enity.FoodItem;
import com.food.ordering.system.foodOrderingSystem.enity.Order;
import com.food.ordering.system.foodOrderingSystem.enity.OrderItem;
import com.food.ordering.system.foodOrderingSystem.enity.User;
import com.food.ordering.system.foodOrderingSystem.repository.FoodItemRepository;
import com.food.ordering.system.foodOrderingSystem.repository.OrderItemRepository;
import com.food.ordering.system.foodOrderingSystem.repository.OrderRepository;
import com.food.ordering.system.foodOrderingSystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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

    public Order placeOrder(OrderRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = new Order();
        order.setUser(user);
        order.setStatus("PLACED");
        order.setCreatedAt(LocalDateTime.now());

        double total = 0;

        // Save order first
        Order savedOrder = orderRepository.save(order);

        for (OrderItemRequest itemReq : request.getItems()) {

            FoodItem food = foodItemRepository.findById(itemReq.getFoodId())
                    .orElseThrow(() -> new RuntimeException("Food not found"));

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
    public List<Order> getOrdersByUser(Long userId) {
        return orderRepository.findByUserId(userId);
    }
}
