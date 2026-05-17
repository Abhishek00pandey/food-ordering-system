package com.food.ordering.system.foodOrderingSystem.service;

import com.food.ordering.system.foodOrderingSystem.entity.FoodItem;
import com.food.ordering.system.foodOrderingSystem.entity.Restaurant;
import com.food.ordering.system.foodOrderingSystem.repository.FoodItemRepository;
import com.food.ordering.system.foodOrderingSystem.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FoodItemService {

    @Autowired
    private FoodItemRepository foodItemRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    public FoodItem addFoodItem(Long restaurantId, FoodItem foodItem) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));

        foodItem.setRestaurant(restaurant);
        if (foodItem.getAvailable() == null) {
            foodItem.setAvailable(true);
        }
        return foodItemRepository.save(foodItem);
    }

    public List<FoodItem> getFoodByRestaurant(Long restaurantId) {
        return foodItemRepository.findByRestaurantId(restaurantId);
    }

    public List<FoodItem> getAllFoods() {
        return foodItemRepository.findAll();
    }

    public FoodItem updateFoodItem(Long id, FoodItem updates) {
        FoodItem existing = foodItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Food item not found"));

        if (updates.getName() != null) existing.setName(updates.getName());
        if (updates.getPrice() != null) existing.setPrice(updates.getPrice());
        if (updates.getDescription() != null) existing.setDescription(updates.getDescription());
        if (updates.getImageUrl() != null) existing.setImageUrl(updates.getImageUrl());
        if (updates.getCategory() != null) existing.setCategory(updates.getCategory());
        if (updates.getAvailable() != null) existing.setAvailable(updates.getAvailable());

        return foodItemRepository.save(existing);
    }

    public void deleteFoodItem(Long id) {
        if (!foodItemRepository.existsById(id)) {
            throw new IllegalArgumentException("Food item not found");
        }
        foodItemRepository.deleteById(id);
    }
}
