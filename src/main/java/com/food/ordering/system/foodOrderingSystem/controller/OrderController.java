package com.food.ordering.system.foodOrderingSystem.controller;

import com.food.ordering.system.foodOrderingSystem.dto.OrderRequest;
import com.food.ordering.system.foodOrderingSystem.dto.OrderResponse;
import com.food.ordering.system.foodOrderingSystem.dto.UpdateOrderStatusRequest;
import com.food.ordering.system.foodOrderingSystem.entity.Order;
import com.food.ordering.system.foodOrderingSystem.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public Order placeOrder(@RequestBody OrderRequest request,
                            Authentication auth) {
        return orderService.placeOrderByEmail(auth.getName(), request);
    }

    @GetMapping("/my")
    public List<OrderResponse> myOrders(Authentication auth) {
        return orderService.getMyOrders(auth.getName());
    }

    @GetMapping("/{id}")
    public OrderResponse getOrder(@PathVariable Long id, Authentication auth) {
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return orderService.getOrderDetail(id, auth.getName(), isAdmin);
    }

    @PutMapping("/{id}/cancel")
    public OrderResponse cancelOrder(@PathVariable Long id, Authentication auth) {
        return orderService.cancelOrder(id, auth.getName());
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public OrderResponse updateStatus(@PathVariable Long id,
                                      @RequestBody UpdateOrderStatusRequest request) {
        return orderService.updateStatus(id, request.getStatus());
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<OrderResponse> allOrders() {
        return orderService.getAllOrders();
    }
}
