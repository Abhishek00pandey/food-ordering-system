package com.food.ordering.system.foodOrderingSystem.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BulkImportResult {
    private int restaurantsCreated = 0;
    private int restaurantsSkipped = 0;
    private int foodItemsCreated = 0;
    private int foodItemsFailed = 0;
    private List<String> errors = new ArrayList<>();

    public void addError(String message) {
        errors.add(message);
    }
}