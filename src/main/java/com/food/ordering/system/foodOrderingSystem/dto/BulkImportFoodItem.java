package com.food.ordering.system.foodOrderingSystem.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BulkImportFoodItem {
    private String name;
    private Double price;
    private String description;

    @JsonProperty("image_url")
    @JsonAlias({"imageUrl"})
    private String imageUrl;

    private String category;
    private Boolean available;
}