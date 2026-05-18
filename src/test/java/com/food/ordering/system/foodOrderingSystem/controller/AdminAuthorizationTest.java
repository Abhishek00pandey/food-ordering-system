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
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AdminAuthorizationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    private String userToken;
    private String adminToken;

    @BeforeEach
    void seedUsers() {
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
    void userCannotCreateRestaurant() throws Exception {
        mockMvc.perform(post("/api/restaurants")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Pizza Place","address":"Main St","rating":4.5}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanCreateRestaurant() throws Exception {
        mockMvc.perform(post("/api/restaurants")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Pizza Place","address":"Main St","rating":4.5}
                                """))
                .andExpect(status().isOk());
    }

    @Test
    void anyoneCanListRestaurants() throws Exception {
        mockMvc.perform(get("/api/restaurants")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());
    }

    @Test
    void anonymousIsRejectedFromProtectedEndpoint() throws Exception {
        mockMvc.perform(get("/api/restaurants"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void userCannotCreateLocation() throws Exception {
        mockMvc.perform(post("/api/locations")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Hanumakonda","label":"Ywca, Ashoka Rd, Hanumakonda","sortOrder":1}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanCreateLocation() throws Exception {
        mockMvc.perform(post("/api/locations")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Warangal","label":"Hunter Rd, Warangal","sortOrder":2}
                                """))
                .andExpect(status().isOk());
    }

    @Test
    void anyAuthenticatedUserCanListLocations() throws Exception {
        mockMvc.perform(get("/api/locations")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());
    }
}
