package com.food.ordering.system.foodOrderingSystem.controller;

import com.food.ordering.system.foodOrderingSystem.entity.FoodItem;
import com.food.ordering.system.foodOrderingSystem.service.FoodItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/foods")
public class FoodItemController {

    @Autowired
    private FoodItemService foodItemService;

    // Add food to restaurant (admin only)
    @PostMapping("/{restaurantId}")
    @PreAuthorize("hasRole('ADMIN')")
    public FoodItem addFood(@PathVariable Long restaurantId,
                            @RequestBody FoodItem foodItem) {
        return foodItemService.addFoodItem(restaurantId, foodItem);
    }

    // Get menu by restaurant
    @GetMapping("/{restaurantId}")
    public List<FoodItem> getFoods(@PathVariable Long restaurantId) {
        return foodItemService.getFoodByRestaurant(restaurantId);
    }

    // Get all foods across restaurants
    @GetMapping
    public List<FoodItem> getAllFoods() {
        return foodItemService.getAllFoods();
    }

    // Update food (admin only)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public FoodItem updateFood(@PathVariable Long id,
                               @RequestBody FoodItem foodItem) {
        return foodItemService.updateFoodItem(id, foodItem);
    }

    // Delete food (admin only)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteFood(@PathVariable Long id) {
        foodItemService.deleteFoodItem(id);
    }
}
