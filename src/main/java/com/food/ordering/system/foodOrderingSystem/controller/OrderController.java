package com.food.ordering.system.foodOrderingSystem.controller;
import com.food.ordering.system.foodOrderingSystem.dto.OrderRequest;
import com.food.ordering.system.foodOrderingSystem.enity.Order;
import com.food.ordering.system.foodOrderingSystem.security.JwtUtil;
import com.food.ordering.system.foodOrderingSystem.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping
    public Order placeOrder(@RequestBody OrderRequest request,
                            HttpServletRequest httpRequest) {

        String authHeader = httpRequest.getHeader("Authorization");
        String token = authHeader.substring(7);

        String email = jwtUtil.extractEmail(token);

        return orderService.placeOrderByEmail(email, request);
    }

    @GetMapping("/user/{userId}")
    public List<Order> getUserOrders(@PathVariable Long userId) {
        return orderService.getOrdersByUser(userId);
    }


}
