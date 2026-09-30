package com.codearena.codearena.security;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.codearena.codearena.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    @Value("${jwt.access-token-expiration}")
    private Duration accessTokenExpiration;

    @Value("${jwt.secret}")
    private String secret;

    private SecretKey getSigningKey() {

        return Keys.hmacShaKeyFor(
                secret.getBytes(
                        StandardCharsets.UTF_8
                )
        );
    }

    public JwtClaims extractJwtClaims(
            String token) {

        Claims claims =
                extractAllClaims(token);

        String email =
                claims.getSubject();

        Long tokenVersion =
                claims.get(
                        "tokenVersion",
                        Long.class
                );

        if (email == null || tokenVersion == null) {
            throw new IllegalArgumentException(
                    "Required JWT claims are missing"
            );
        }

        return new JwtClaims(
                email,
                tokenVersion
        );
    }

    private Claims extractAllClaims(
            String token) {

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String generateToken(User user) {

        SecretKey key =
                getSigningKey();

        return Jwts.builder()
                .subject(
                        user.getEmail()
                )
                .claim(
                        "tokenVersion",
                        user.getTokenVersion()
                )
                .issuedAt(
                        new Date()
                )
                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + accessTokenExpiration.toMillis()
                        )
                )
                .signWith(key)
                .compact();
    }

    public String extractEmail(
            String token) {

        Claims claims =
                extractAllClaims(token);

        return claims.getSubject();
    }

    public long extractTokenVersion(
            String token) {

        Claims claims =
                extractAllClaims(token);

        Long tokenVersion =
                claims.get(
                        "tokenVersion",
                        Long.class
                );

        if (tokenVersion == null) {
            throw new IllegalArgumentException(
                    "Token version is missing"
            );
        }

        return tokenVersion;
    }
}
