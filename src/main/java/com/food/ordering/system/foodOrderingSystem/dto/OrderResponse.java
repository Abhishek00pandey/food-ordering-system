package com.food.ordering.system.foodOrderingSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private Double totalAmount;
    private String status;
    private String deliveryAddress;
    private String phone;
    private LocalDateTime createdAt;
    private String userEmail;
    private String userName;
    private List<OrderItemResponse> items;
}
