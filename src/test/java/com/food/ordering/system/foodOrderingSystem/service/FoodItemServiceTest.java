package com.food.ordering.system.foodOrderingSystem.service;

import com.food.ordering.system.foodOrderingSystem.entity.FoodItem;
import com.food.ordering.system.foodOrderingSystem.entity.Restaurant;
import com.food.ordering.system.foodOrderingSystem.repository.FoodItemRepository;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FoodItemServiceTest {

    @Mock
    private FoodItemRepository foodItemRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private FoodItemService foodItemService;

    @Test
    void addFoodItemAttachesRestaurantAndDefaultsAvailableTrue() {
        Restaurant r = new Restaurant();
        r.setId(7L);
        when(restaurantRepository.findById(7L)).thenReturn(Optional.of(r));

        FoodItem incoming = new FoodItem();
        incoming.setName("Pizza");
        incoming.setPrice(199.0);
        incoming.setAvailable(null);

        when(foodItemRepository.save(any(FoodItem.class))).thenAnswer(inv -> inv.getArgument(0));

        FoodItem saved = foodItemService.addFoodItem(7L, incoming);

        assertThat(saved.getRestaurant()).isSameAs(r);
        assertThat(saved.getAvailable()).isTrue();
    }

    @Test
    void addFoodItemFailsWhenRestaurantMissing() {
        when(restaurantRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> foodItemService.addFoodItem(99L, new FoodItem()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void updateFoodItemAppliesOnlyNonNullFields() {
        FoodItem existing = new FoodItem();
        existing.setId(1L);
        existing.setName("Old");
        existing.setPrice(100.0);
        existing.setDescription("old desc");
        existing.setAvailable(true);

        FoodItem patch = new FoodItem();
        patch.setName("New Name");
        // price + description left null → should not overwrite

        when(foodItemRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(foodItemRepository.save(any(FoodItem.class))).thenAnswer(inv -> inv.getArgument(0));

        FoodItem updated = foodItemService.updateFoodItem(1L, patch);

        assertThat(updated.getName()).isEqualTo("New Name");
        assertThat(updated.getPrice()).isEqualTo(100.0);
        assertThat(updated.getDescription()).isEqualTo("old desc");
    }

    @Test
    void deleteFoodItemFailsForUnknownId() {
        when(foodItemRepository.existsById(123L)).thenReturn(false);
        assertThatThrownBy(() -> foodItemService.deleteFoodItem(123L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deleteFoodItemCallsRepository() {
        when(foodItemRepository.existsById(1L)).thenReturn(true);
        foodItemService.deleteFoodItem(1L);
        verify(foodItemRepository).deleteById(1L);
    }
}
