package com.food.ordering.system.foodOrderingSystem.repository;

import com.food.ordering.system.foodOrderingSystem.entity.FoodItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FoodItemRepository extends JpaRepository<FoodItem,Long> {
    List<FoodItem> findByRestaurantId(Long restaurantId);

    @Modifying
    @Query("DELETE FROM FoodItem f WHERE f.restaurant.id = :restaurantId")
    void deleteByRestaurantId(@Param("restaurantId") Long restaurantId);
}