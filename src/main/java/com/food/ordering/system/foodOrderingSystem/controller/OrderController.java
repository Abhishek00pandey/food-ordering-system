package com.food.ordering.system.foodOrderingSystem.controller;



import com.food.ordering.system.foodOrderingSystem.dto.OrderRequest;
import com.food.ordering.system.foodOrderingSystem.enity.Order;
import com.food.ordering.system.foodOrderingSystem.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public Order placeOrder(@RequestBody OrderRequest request) {
        return orderService.placeOrder(request);
    }

    @GetMapping("/user/{userId}")
    public List<Order> getUserOrders(@PathVariable Long userId) {
        return orderService.getOrdersByUser(userId);
    }


}
