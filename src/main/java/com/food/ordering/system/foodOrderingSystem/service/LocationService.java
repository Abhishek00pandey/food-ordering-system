package com.food.ordering.system.foodOrderingSystem.service;

import com.food.ordering.system.foodOrderingSystem.entity.Location;
import com.food.ordering.system.foodOrderingSystem.repository.LocationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationService {

    private static final Logger log = LoggerFactory.getLogger(LocationService.class);
    private static final int SOFT_CAP = 5;

    @Autowired
    private LocationRepository locationRepository;

    public List<Location> getAllLocations() {
        return locationRepository.findAllByOrderBySortOrderAscNameAsc();
    }

    public Location getLocation(Long id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Location not found"));
    }

    public Location addLocation(Location location) {
        if (location.getName() == null || location.getName().isBlank()) {
            throw new IllegalArgumentException("Location name is required");
        }
        if (location.getLabel() == null || location.getLabel().isBlank()) {
            throw new IllegalArgumentException("Location label is required");
        }
        Location saved = locationRepository.save(location);
        long count = locationRepository.count();
        if (count > SOFT_CAP) {
            log.warn("Location count ({}) exceeds recommended cap of {}", count, SOFT_CAP);
        }
        return saved;
    }

    public Location updateLocation(Long id, Location updates) {
        Location existing = getLocation(id);
        if (updates.getName() != null) existing.setName(updates.getName());
        if (updates.getLabel() != null) existing.setLabel(updates.getLabel());
        if (updates.getSortOrder() != null) existing.setSortOrder(updates.getSortOrder());
        return locationRepository.save(existing);
    }

    public void deleteLocation(Long id) {
        if (!locationRepository.existsById(id)) {
            throw new IllegalArgumentException("Location not found");
        }
        locationRepository.deleteById(id);
    }
}
