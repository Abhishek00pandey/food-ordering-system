package com.food.ordering.system.foodOrderingSystem.security;

import com.food.ordering.system.foodOrderingSystem.dto.AuthResponse;
import com.food.ordering.system.foodOrderingSystem.dto.LoginRequest;
import com.food.ordering.system.foodOrderingSystem.dto.RegisterRequest;
import com.food.ordering.system.foodOrderingSystem.entity.Role;
import com.food.ordering.system.foodOrderingSystem.entity.User;
import com.food.ordering.system.foodOrderingSystem.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setName("Alice");
        registerRequest.setEmail("alice@example.com");
        registerRequest.setPassword("secret123");
    }

    @Test
    void registerSucceedsForNewEmail() {
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("secret123")).thenReturn("HASHED");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });
        when(jwtUtil.generateToken(eq("alice@example.com"), eq(Role.USER))).thenReturn("token-xyz");

        AuthResponse res = authService.register(registerRequest);

        assertThat(res.getToken()).isEqualTo("token-xyz");
        assertThat(res.getEmail()).isEqualTo("alice@example.com");
        assertThat(res.getRole()).isEqualTo("USER");
    }

    @Test
    void registerRejectsDuplicateEmail() {
        when(userRepository.findByEmail("alice@example.com"))
                .thenReturn(Optional.of(new User()));

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email already registered");
    }

    @Test
    void registerRejectsMissingFields() {
        RegisterRequest empty = new RegisterRequest();
        assertThatThrownBy(() -> authService.register(empty))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void loginSucceedsWithCorrectPassword() {
        User stored = new User();
        stored.setEmail("alice@example.com");
        stored.setName("Alice");
        stored.setPassword("HASHED");
        stored.setRole(Role.USER);

        LoginRequest login = new LoginRequest();
        login.setEmail("alice@example.com");
        login.setPassword("secret123");

        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(stored));
        when(passwordEncoder.matches("secret123", "HASHED")).thenReturn(true);
        when(jwtUtil.generateToken("alice@example.com", Role.USER)).thenReturn("token-xyz");

        AuthResponse res = authService.login(login);

        assertThat(res.getToken()).isEqualTo("token-xyz");
        assertThat(res.getName()).isEqualTo("Alice");
    }

    @Test
    void loginRejectsWrongPassword() {
        User stored = new User();
        stored.setEmail("alice@example.com");
        stored.setPassword("HASHED");
        stored.setRole(Role.USER);

        LoginRequest login = new LoginRequest();
        login.setEmail("alice@example.com");
        login.setPassword("wrong");

        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(stored));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThatThrownBy(() -> authService.login(login))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid email or password");
    }

    @Test
    void loginRejectsUnknownEmail() {
        LoginRequest login = new LoginRequest();
        login.setEmail("ghost@example.com");
        login.setPassword("anything");

        when(userRepository.findByEmail("ghost@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(login))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
