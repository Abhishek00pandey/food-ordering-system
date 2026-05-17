package com.food.ordering.system.foodOrderingSystem.controller;

import com.food.ordering.system.foodOrderingSystem.dto.OrderResponse;
import com.food.ordering.system.foodOrderingSystem.dto.UpdateRoleRequest;
import com.food.ordering.system.foodOrderingSystem.dto.UserResponse;
import com.food.ordering.system.foodOrderingSystem.service.OrderService;
import com.food.ordering.system.foodOrderingSystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @GetMapping("/users")
    public List<UserResponse> listUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/orders")
    public List<OrderResponse> listOrders(@RequestParam(required = false) String status) {
        List<OrderResponse> all = orderService.getAllOrders();
        if (status == null || status.isBlank()) {
            return all;
        }
        return all.stream().filter(o -> status.equalsIgnoreCase(o.getStatus())).toList();
    }

    @PutMapping("/users/{id}/role")
    public UserResponse updateUserRole(@PathVariable Long id,
                                       @RequestBody UpdateRoleRequest request,
                                       Authentication auth) {
        return userService.updateRole(id, request.getRole(), auth.getName());
    }
}
