package com.food.ordering.system.foodOrderingSystem.dto;


import lombok.Data;

@Data
public class OrderItemRequest {

    private  Long foodId;
    private  int quantity;
}
