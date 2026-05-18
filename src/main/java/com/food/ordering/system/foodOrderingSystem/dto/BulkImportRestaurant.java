package com.food.ordering.system.foodOrderingSystem.dto;

import lombok.Data;

import java.util.List;

@Data
public class BulkImportRestaurant {
    private String name;
    private String address;
    private Double rating;
    private String locationName;
    private Long locationId;
    private List<BulkImportFoodItem> menu;
}