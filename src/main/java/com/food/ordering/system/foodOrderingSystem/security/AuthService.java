package com.food.ordering.system.foodOrderingSystem.security;

import com.food.ordering.system.foodOrderingSystem.enity.User;
import com.food.ordering.system.foodOrderingSystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    public String register(User user) {
        user.setRole("USER");
        userRepository.save(user);
        return "User registered";
    }

    public String login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Invalid password");
        }

        return jwtUtil.generateToken(email);
    }
}
