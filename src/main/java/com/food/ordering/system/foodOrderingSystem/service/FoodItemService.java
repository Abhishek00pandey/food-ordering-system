package com.food.ordering.system.foodOrderingSystem.service;

import com.food.ordering.system.foodOrderingSystem.enity.FoodItem;
import com.food.ordering.system.foodOrderingSystem.enity.Restaurant;
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
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        foodItem.setRestaurant(restaurant);
        return foodItemRepository.save(foodItem);
    }

    public List<FoodItem> getFoodByRestaurant(Long restaurantId) {
        return foodItemRepository.findByRestaurantId(restaurantId);
    }
}
