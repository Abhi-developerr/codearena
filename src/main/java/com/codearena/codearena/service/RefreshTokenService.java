package com.codearena.codearena.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.codearena.codearena.dto.RefreshResponse;
import com.codearena.codearena.entity.RefreshToken;
import com.codearena.codearena.entity.User;
import com.codearena.codearena.exception.RefreshTokenExpiredException;
import com.codearena.codearena.exception.RefreshTokenNotFoundException;
import com.codearena.codearena.repository.RefreshTokenRepository;
import com.codearena.codearena.security.JwtService;

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

           throw new RefreshTokenExpiredException(
        "Refresh token has expired"
);
        }

        return refreshToken;
    }

    public RefreshToken findByToken(String token) {

    return refreshTokenRepository
            .findByToken(token)
            .orElseThrow(() ->
        new RefreshTokenNotFoundException(
                "Refresh token not found"
        )
);
}

public RefreshResponse refreshAccessToken(
        String token) {

    RefreshToken oldRefreshToken =
            findByToken(token);

    verifyExpiration(oldRefreshToken);

    User user =
            oldRefreshToken.getUser();

    refreshTokenRepository.delete(
            oldRefreshToken
    );

    RefreshToken newRefreshToken =
            createRefreshToken(user);

    String newAccessToken =
            jwtService.generateToken(user);

    return new RefreshResponse(
            newAccessToken,
            newRefreshToken.getToken()
    );
}

public void deleteByToken(String token) {

    RefreshToken refreshToken =
            findByToken(token);

    refreshTokenRepository.delete(
            refreshToken
    );
}
}