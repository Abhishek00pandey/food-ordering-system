package com.food.ordering.system.foodOrderingSystem.repository;

import com.food.ordering.system.foodOrderingSystem.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {
    List<Location> findAllByOrderBySortOrderAscNameAsc();

    Optional<Location> findByNameIgnoreCase(String name);
}
