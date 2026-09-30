package com.codearena.codearena.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Field;
import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.codearena.codearena.entity.Role;
import com.codearena.codearena.entity.User;

import io.jsonwebtoken.JwtException;

class JwtServiceTest {

    private JwtService jwtService;

    private User user;

    @BeforeEach
    void setUp() throws Exception {

        jwtService =
                new JwtService();

        setField(
                "secret",
                "01234567890123456789012345678901"
        );

        setField(
                "accessTokenExpiration",
                Duration.ofMinutes(5)
        );

        user = new User();

        user.setEmail(
                "user@example.com"
        );

        user.setRole(
                Role.USER
        );

        user.setTokenVersion(3L);
    }

    @Test
    void generatedTokenContainsEmailAndTokenVersion() {

        String token =
                jwtService.generateToken(user);

        JwtClaims claims =
                jwtService.extractJwtClaims(token);

        assertEquals(
                "user@example.com",
                claims.getEmail()
        );

        assertEquals(
                3L,
                claims.getTokenVersion()
        );
    }

    @Test
    void extractEmailReturnsSubject() {

        String token =
                jwtService.generateToken(user);

        assertEquals(
                "user@example.com",
                jwtService.extractEmail(token)
        );
    }

    @Test
    void extractTokenVersionReturnsVersion() {

        String token =
                jwtService.generateToken(user);

        assertEquals(
                3L,
                jwtService.extractTokenVersion(token)
        );
    }

    @Test
    void tamperedTokenIsRejected() {

        String token =
                jwtService.generateToken(user);

        assertThrows(
                JwtException.class,
                () -> jwtService.extractJwtClaims(
                        token + "tampered"
                )
        );
    }

    @Test
    void malformedTokenIsRejected() {

        assertThrows(
                JwtException.class,
                () -> jwtService.extractJwtClaims(
                        "invalid-token"
                )
        );
    }

    private void setField(
            String name,
            Object value)
            throws Exception {

        Field field =
                JwtService.class.getDeclaredField(
                        name
                );

        field.setAccessible(true);

        field.set(
                jwtService,
                value
        );
    }
}
