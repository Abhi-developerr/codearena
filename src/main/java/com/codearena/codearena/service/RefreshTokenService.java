package com.codearena.service;

import com.codearena.entity.RefreshToken;
import com.codearena.entity.User;
import com.codearena.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository) {

        this.refreshTokenRepository =
                refreshTokenRepository;
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
}