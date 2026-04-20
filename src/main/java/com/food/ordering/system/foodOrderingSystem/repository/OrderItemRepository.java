package com.food.ordering.system.foodOrderingSystem.repository;

import com.food.ordering.system.foodOrderingSystem.enity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

}
