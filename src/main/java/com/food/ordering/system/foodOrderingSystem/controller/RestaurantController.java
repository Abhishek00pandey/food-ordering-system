package com.food.ordering.system.foodOrderingSystem.controller;


import com.food.ordering.system.foodOrderingSystem.enity.Restaurant;
import com.food.ordering.system.foodOrderingSystem.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    // ✅ Add restaurant API (Admin)
    @PostMapping
    public Restaurant addRestaurant(@RequestBody Restaurant restaurant) {
        return restaurantService.addRestaurant(restaurant);
    }

    // ✅ Get all restaurants API (User)
    @GetMapping
    public List<Restaurant> getAllRestaurants() {
        return restaurantService.getAllRestaurants();
    }
}
