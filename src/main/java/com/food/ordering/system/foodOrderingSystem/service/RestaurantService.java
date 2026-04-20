package com.food.ordering.system.foodOrderingSystem.service;

import com.food.ordering.system.foodOrderingSystem.enity.Restaurant;
import com.food.ordering.system.foodOrderingSystem.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RestaurantService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    public Restaurant addRestaurant(Restaurant restaurant) {
        return restaurantRepository.save(restaurant);
    }

    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.findAll();
    }
}
