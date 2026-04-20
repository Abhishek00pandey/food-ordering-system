package com.food.ordering.system.foodOrderingSystem.enity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class FoodItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Double price;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

}
