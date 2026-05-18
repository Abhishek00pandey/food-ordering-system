package com.food.ordering.system.foodOrderingSystem.repository;

import com.food.ordering.system.foodOrderingSystem.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    List<Restaurant> findByLocationId(Long locationId);

    Optional<Restaurant> findByNameIgnoreCase(String name);

    @Query("""
            SELECT r FROM Restaurant r
            WHERE (:locationId IS NULL OR r.location.id = :locationId)
              AND (
                LOWER(r.name) LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(COALESCE(r.address, '')) LIKE LOWER(CONCAT('%', :q, '%'))
                OR r.id IN (
                  SELECT f.restaurant.id FROM FoodItem f
                  WHERE LOWER(f.name) LIKE LOWER(CONCAT('%', :q, '%'))
                     OR LOWER(COALESCE(f.category, '')) LIKE LOWER(CONCAT('%', :q, '%'))
                     OR LOWER(COALESCE(f.description, '')) LIKE LOWER(CONCAT('%', :q, '%'))
                )
              )
            """)
    List<Restaurant> searchRestaurants(@Param("q") String q,
                                       @Param("locationId") Long locationId);
}