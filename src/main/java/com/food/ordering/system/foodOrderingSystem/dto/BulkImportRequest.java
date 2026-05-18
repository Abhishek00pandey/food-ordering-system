package com.food.ordering.system.foodOrderingSystem.dto;

import lombok.Data;

import java.util.List;

@Data
public class BulkImportRequest {
    private String defaultLocationName;
    private Long defaultLocationId;
    private List<BulkImportRestaurant> restaurants;
}