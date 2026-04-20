package com.food.ordering.system.foodOrderingSystem.repository;

import com.food.ordering.system.foodOrderingSystem.enity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepository extends JpaRepository<Restaurant,Long> {
}
