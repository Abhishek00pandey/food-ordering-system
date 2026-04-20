package com.food.ordering.system.foodOrderingSystem.repository;

import com.food.ordering.system.foodOrderingSystem.enity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User > findByEmail(String email);

}
