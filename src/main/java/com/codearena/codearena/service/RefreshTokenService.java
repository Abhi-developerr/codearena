package com.codearena.codearena.service;

import com.codearena.codearena.entity.RefreshToken;
import com.codearena.codearena.entity.User;
import com.codearena.codearena.repository.RefreshTokenRepository;
import com.codearena.codearena.security.JwtService;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
private final JwtService jwtService;

    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository,
            JwtService jwtService) {

        this.refreshTokenRepository =
                refreshTokenRepository;
        this.jwtService = jwtService;
    }

    public RefreshToken createRefreshToken(User user) {

        RefreshToken refreshToken =
                new RefreshToken();

        refreshToken.setToken(
                UUID.randomUUID().toString()
        );

        refreshToken.setExpiresAt(
                Instant.now().plusSeconds(
                        7 * 24 * 60 * 60
                )
        );

        refreshToken.setUser(user);

        return refreshTokenRepository.save(
                refreshToken
        );
    }

    public RefreshToken verifyExpiration(
            RefreshToken refreshToken) {

        if (refreshToken.getExpiresAt()
                .isBefore(Instant.now())) {

            refreshTokenRepository.delete(
                    refreshToken
            );

            throw new RuntimeException(
                    "Refresh token has expired"
            );
        }

        return refreshToken;
    }

    public RefreshToken findByToken(String token) {

    return refreshTokenRepository
            .findByToken(token)
            .orElseThrow(() ->
                    new RuntimeException(
                            "Refresh token not found"
                    )
            );
}

public String refreshAccessToken(String token) {

    RefreshToken refreshToken =
            findByToken(token);

    verifyExpiration(refreshToken);

    User user =
            refreshToken.getUser();

    return jwtService.generateToken(user);
}
}