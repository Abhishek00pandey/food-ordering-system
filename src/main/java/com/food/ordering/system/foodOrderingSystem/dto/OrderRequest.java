package com.food.ordering.system.foodOrderingSystem.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {
    private String deliveryAddress;
    private String phone;
    private List<OrderItemRequest> items;
}
