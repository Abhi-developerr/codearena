package com.codearena.codearena.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.codearena.codearena.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

   public String generateToken(User user) {

    SecretKey key = Keys.hmacShaKeyFor(
            secret.getBytes(StandardCharsets.UTF_8)
    );

    return Jwts.builder()
            .subject(user.getEmail())
            .claim(
                "tokenVersion",
                user.getTokenVersion()
            )
            .issuedAt(new Date())
            .expiration(
                    new Date(
                            System.currentTimeMillis()
                                    + expiration
                    )
            )
            .signWith(key)
            .compact();
}
    public String extractEmail(String token) {

    SecretKey key = Keys.hmacShaKeyFor(
            secret.getBytes(StandardCharsets.UTF_8)
    );

    Claims claims = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();

    return claims.getSubject();
}

public boolean isTokenValid(
        String token,
        UserDetails userDetails) {

    try {

        String email = extractEmail(token);

        return email.equals(userDetails.getUsername());

    } catch (JwtException | IllegalArgumentException exception) {

        return false;
    }
}

public Long extractTokenVersion(String token) {

    SecretKey key = Keys.hmacShaKeyFor(
            secret.getBytes(StandardCharsets.UTF_8)
    );

    Claims claims = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload();

    return claims.get("tokenVersion", Long.class);
}
}