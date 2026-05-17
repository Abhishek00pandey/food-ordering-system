package com.food.ordering.system.foodOrderingSystem.security;

import com.food.ordering.system.foodOrderingSystem.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret",
                "test-secret-key-for-unit-tests-needs-to-be-at-least-32-chars-long");
        ReflectionTestUtils.setField(jwtUtil, "expirationMs", 3600000L);
    }

    @Test
    void generateAndExtractEmail() {
        String token = jwtUtil.generateToken("user@example.com", Role.USER);
        assertThat(token).isNotBlank();
        assertThat(jwtUtil.extractEmail(token)).isEqualTo("user@example.com");
    }

    @Test
    void roleClaimIsPreserved() {
        String token = jwtUtil.generateToken("admin@example.com", Role.ADMIN);
        assertThat(jwtUtil.extractRole(token)).isEqualTo("ADMIN");
    }

    @Test
    void expiredTokenIsRejected() {
        ReflectionTestUtils.setField(jwtUtil, "expirationMs", -1000L);
        String token = jwtUtil.generateToken("user@example.com", Role.USER);

        assertThatThrownBy(() -> jwtUtil.extractEmail(token))
                .isInstanceOf(Exception.class);
    }

    @Test
    void tamperedTokenIsRejected() {
        String token = jwtUtil.generateToken("user@example.com", Role.USER);
        String tampered = token.substring(0, token.length() - 5) + "AAAAA";

        assertThatThrownBy(() -> jwtUtil.extractEmail(tampered))
                .isInstanceOf(Exception.class);
    }
}
