package com.food.ordering.system.foodOrderingSystem.service;

import com.food.ordering.system.foodOrderingSystem.dto.BulkImportFoodItem;
import com.food.ordering.system.foodOrderingSystem.dto.BulkImportRequest;
import com.food.ordering.system.foodOrderingSystem.dto.BulkImportRestaurant;
import com.food.ordering.system.foodOrderingSystem.dto.BulkImportResult;
import com.food.ordering.system.foodOrderingSystem.entity.FoodItem;
import com.food.ordering.system.foodOrderingSystem.entity.Location;
import com.food.ordering.system.foodOrderingSystem.entity.Restaurant;
import com.food.ordering.system.foodOrderingSystem.repository.FoodItemRepository;
import com.food.ordering.system.foodOrderingSystem.repository.LocationRepository;
import com.food.ordering.system.foodOrderingSystem.repository.RestaurantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BulkImportService {

    private static final Logger log = LoggerFactory.getLogger(BulkImportService.class);

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private FoodItemRepository foodItemRepository;

    @Autowired
    private LocationRepository locationRepository;

    public BulkImportResult importBulk(BulkImportRequest request) {
        BulkImportResult result = new BulkImportResult();

        if (request == null || request.getRestaurants() == null || request.getRestaurants().isEmpty()) {
            result.addError("No restaurants provided in the import payload.");
            return result;
        }

        Location defaultLocation = resolveLocation(request.getDefaultLocationId(),
                request.getDefaultLocationName());

        for (int i = 0; i < request.getRestaurants().size(); i++) {
            BulkImportRestaurant entry = request.getRestaurants().get(i);
            try {
                processRestaurant(entry, i, defaultLocation, result);
            } catch (RuntimeException ex) {
                log.warn("Bulk import row {} failed: {}", i, ex.getMessage());
                result.addError("Restaurant[" + i + "] '"
                        + (entry == null ? "" : entry.getName()) + "': " + ex.getMessage());
            }
        }
        return result;
    }

    private void processRestaurant(BulkImportRestaurant entry,
                                   int index,
                                   Location defaultLocation,
                                   BulkImportResult result) {
        if (entry == null || entry.getName() == null || entry.getName().isBlank()) {
            result.addError("Restaurant[" + index + "]: missing name");
            return;
        }

        Optional<Restaurant> existing = restaurantRepository.findByNameIgnoreCase(entry.getName().trim());
        if (existing.isPresent()) {
            result.setRestaurantsSkipped(result.getRestaurantsSkipped() + 1);
            return;
        }

        Location location;
        try {
            Location override = resolveLocation(entry.getLocationId(), entry.getLocationName());
            location = override != null ? override : defaultLocation;
        } catch (IllegalArgumentException ex) {
            result.addError("Restaurant[" + index + "] '" + entry.getName() + "': " + ex.getMessage());
            return;
        }

        Restaurant restaurant = new Restaurant();
        restaurant.setName(entry.getName().trim());
        restaurant.setAddress(entry.getAddress());
        restaurant.setRating(entry.getRating());
        restaurant.setLocation(location);
        Restaurant saved = restaurantRepository.save(restaurant);
        result.setRestaurantsCreated(result.getRestaurantsCreated() + 1);

        List<BulkImportFoodItem> menu = entry.getMenu();
        if (menu == null) return;
        for (int j = 0; j < menu.size(); j++) {
            BulkImportFoodItem item = menu.get(j);
            try {
                saveFoodItem(saved, item, index, j, result);
            } catch (RuntimeException ex) {
                result.setFoodItemsFailed(result.getFoodItemsFailed() + 1);
                result.addError("Restaurant[" + index + "] '" + entry.getName()
                        + "' menu[" + j + "]: " + ex.getMessage());
            }
        }
    }

    private void saveFoodItem(Restaurant restaurant,
                              BulkImportFoodItem item,
                              int restaurantIndex,
                              int menuIndex,
                              BulkImportResult result) {
        if (item == null || item.getName() == null || item.getName().isBlank()) {
            result.setFoodItemsFailed(result.getFoodItemsFailed() + 1);
            result.addError("Restaurant[" + restaurantIndex + "] '" + restaurant.getName()
                    + "' menu[" + menuIndex + "]: missing item name");
            return;
        }
        if (item.getPrice() == null) {
            result.setFoodItemsFailed(result.getFoodItemsFailed() + 1);
            result.addError("Restaurant[" + restaurantIndex + "] '" + restaurant.getName()
                    + "' menu[" + menuIndex + "] '" + item.getName() + "': missing price");
            return;
        }
        FoodItem food = new FoodItem();
        food.setName(item.getName().trim());
        food.setPrice(item.getPrice());
        food.setDescription(item.getDescription());
        food.setImageUrl(item.getImageUrl());
        food.setCategory(item.getCategory());
        food.setAvailable(item.getAvailable() == null ? Boolean.TRUE : item.getAvailable());
        food.setRestaurant(restaurant);
        foodItemRepository.save(food);
        result.setFoodItemsCreated(result.getFoodItemsCreated() + 1);
    }

    private Location resolveLocation(Long id, String name) {
        if (id != null) {
            return locationRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Location id " + id + " not found"));
        }
        if (name != null && !name.isBlank()) {
            return locationRepository.findByNameIgnoreCase(name.trim())
                    .orElseThrow(() -> new IllegalArgumentException("Location '" + name + "' not found"));
        }
        return null;
    }
}