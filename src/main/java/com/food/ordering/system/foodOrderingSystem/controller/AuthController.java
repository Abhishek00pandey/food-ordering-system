package com.food.ordering.system.foodOrderingSystem.controller;


import com.food.ordering.system.foodOrderingSystem.dto.AuthResponse;
import com.food.ordering.system.foodOrderingSystem.dto.LoginRequest;
import com.food.ordering.system.foodOrderingSystem.dto.RegisterRequest;
import com.food.ordering.system.foodOrderingSystem.dto.UserResponse;
import com.food.ordering.system.foodOrderingSystem.security.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/me")
    public UserResponse me(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalArgumentException("Not authenticated");
        }
        return authService.currentUser(authentication.getName());
    }
}
