package com.food.ordering.system.foodOrderingSystem.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
