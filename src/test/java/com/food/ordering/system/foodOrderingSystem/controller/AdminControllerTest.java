package com.food.ordering.system.foodOrderingSystem.controller;

import com.food.ordering.system.foodOrderingSystem.entity.Role;
import com.food.ordering.system.foodOrderingSystem.entity.User;
import com.food.ordering.system.foodOrderingSystem.repository.UserRepository;
import com.food.ordering.system.foodOrderingSystem.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AdminControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtUtil jwtUtil;

    private String userToken;
    private String adminToken;

    @BeforeEach
    void seed() {
        userRepository.deleteAll();

        User user = new User();
        user.setName("Plain");
        user.setEmail("plain@example.com");
        user.setPassword(passwordEncoder.encode("pw"));
        user.setRole(Role.USER);
        userRepository.save(user);

        User admin = new User();
        admin.setName("Boss");
        admin.setEmail("admin@example.com");
        admin.setPassword(passwordEncoder.encode("pw"));
        admin.setRole(Role.ADMIN);
        userRepository.save(admin);

        userToken = jwtUtil.generateToken(user.getEmail(), Role.USER);
        adminToken = jwtUtil.generateToken(admin.getEmail(), Role.ADMIN);
    }

    @Test
    void adminListsUsers() throws Exception {
        mockMvc.perform(get("/api/admin/users")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void userCannotListUsers() throws Exception {
        mockMvc.perform(get("/api/admin/users")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void anonymousCannotHitAdminEndpoint() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void adminListsOrdersWithStatusFilter() throws Exception {
        mockMvc.perform(get("/api/admin/orders").param("status", "PLACED")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }
}
