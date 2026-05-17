package com.food.ordering.system.foodOrderingSystem.service;

import com.food.ordering.system.foodOrderingSystem.entity.Restaurant;
import com.food.ordering.system.foodOrderingSystem.repository.RestaurantRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private RestaurantService restaurantService;

    @Test
    void getRestaurantThrowsWhenMissing() {
        when(restaurantRepository.findById(404L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> restaurantService.getRestaurant(404L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void updateRestaurantAppliesPartialFields() {
        Restaurant existing = new Restaurant();
        existing.setId(1L);
        existing.setName("Old");
        existing.setAddress("Old Addr");
        existing.setRating(4.0);

        Restaurant patch = new Restaurant();
        patch.setRating(4.8);

        when(restaurantRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(restaurantRepository.save(any(Restaurant.class))).thenAnswer(inv -> inv.getArgument(0));

        Restaurant updated = restaurantService.updateRestaurant(1L, patch);

        assertThat(updated.getName()).isEqualTo("Old");
        assertThat(updated.getAddress()).isEqualTo("Old Addr");
        assertThat(updated.getRating()).isEqualTo(4.8);
    }

    @Test
    void deleteRejectsUnknownId() {
        when(restaurantRepository.existsById(99L)).thenReturn(false);
        assertThatThrownBy(() -> restaurantService.deleteRestaurant(99L))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
