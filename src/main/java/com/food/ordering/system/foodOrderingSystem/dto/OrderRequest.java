package com.food.ordering.system.foodOrderingSystem.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {
    private Long userId;
    private List<OrderItemRequest> items;
}
