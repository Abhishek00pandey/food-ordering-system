package com.food.ordering.system.foodOrderingSystem.repository;

import com.food.ordering.system.foodOrderingSystem.enity.FoodItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FoodItemRepository extends JpaRepository<FoodItem,Long> {
    List<FoodItem> findByRestaurantId(Long restaurantId);

}
