package com.food.ordering.system.foodOrderingSystem.service;

import com.food.ordering.system.foodOrderingSystem.entity.Location;
import com.food.ordering.system.foodOrderingSystem.entity.Restaurant;
import com.food.ordering.system.foodOrderingSystem.repository.FoodItemRepository;
import com.food.ordering.system.foodOrderingSystem.repository.LocationRepository;
import com.food.ordering.system.foodOrderingSystem.repository.OrderItemRepository;
import com.food.ordering.system.foodOrderingSystem.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RestaurantService {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private FoodItemRepository foodItemRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    public Restaurant addRestaurant(Restaurant restaurant) {
        attachLocation(restaurant);
        return restaurantRepository.save(restaurant);
    }

    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.findAll();
    }

    public List<Restaurant> getRestaurantsByLocation(Long locationId) {
        return restaurantRepository.findByLocationId(locationId);
    }

    public List<Restaurant> searchRestaurants(String query, Long locationId) {
        String trimmed = query == null ? "" : query.trim();
        if (trimmed.isEmpty()) {
            return locationId == null
                    ? restaurantRepository.findAll()
                    : restaurantRepository.findByLocationId(locationId);
        }
        return restaurantRepository.searchRestaurants(trimmed, locationId);
    }

    public Restaurant getRestaurant(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));
    }

    public Restaurant updateRestaurant(Long id, Restaurant updates) {
        Restaurant existing = getRestaurant(id);
        if (updates.getName() != null) existing.setName(updates.getName());
        if (updates.getAddress() != null) existing.setAddress(updates.getAddress());
        if (updates.getRating() != null) existing.setRating(updates.getRating());
        if (updates.getLocation() != null) {
            attachLocation(updates);
            existing.setLocation(updates.getLocation());
        }
        return restaurantRepository.save(existing);
    }

    @Transactional
    public void deleteRestaurant(Long id) {
        if (!restaurantRepository.existsById(id)) {
            throw new IllegalArgumentException("Restaurant not found");
        }
        if (orderItemRepository.existsByFoodItem_Restaurant_Id(id)) {
            throw new IllegalArgumentException(
                    "Cannot delete: this restaurant has order history. " +
                    "Delete the related orders first, or mark its items as unavailable.");
        }
        foodItemRepository.deleteByRestaurantId(id);
        restaurantRepository.deleteById(id);
    }

    private void attachLocation(Restaurant restaurant) {
        Location loc = restaurant.getLocation();
        if (loc != null && loc.getId() != null) {
            Location managed = locationRepository.findById(loc.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Location not found"));
            restaurant.setLocation(managed);
        } else {
            restaurant.setLocation(null);
        }
    }
}