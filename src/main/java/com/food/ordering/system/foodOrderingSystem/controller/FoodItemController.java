package com.food.ordering.system.foodOrderingSystem.controller;

import com.food.ordering.system.foodOrderingSystem.enity.FoodItem;
import com.food.ordering.system.foodOrderingSystem.service.FoodItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/foods")
public class FoodItemController {

    @Autowired
    private FoodItemService foodItemService;

    // ✅ Add food to restaurant
    @PostMapping("/{restaurantId}")
    public FoodItem addFood(@PathVariable Long restaurantId,
                            @RequestBody FoodItem foodItem) {
        return foodItemService.addFoodItem(restaurantId, foodItem);
    }

    // ✅ Get menu by restaurant
    @GetMapping("/{restaurantId}")
    public List<FoodItem> getFoods(@PathVariable Long restaurantId) {
        return foodItemService.getFoodByRestaurant(restaurantId);
    }
}
