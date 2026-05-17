package com.food.ordering.system.foodOrderingSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderItemResponse {
    private Long id;
    private Long foodId;
    private String foodName;
    private Double price;
    private int quantity;
    private Double lineTotal;
}
