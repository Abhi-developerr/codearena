package com.codearena.codearena.service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.codearena.codearena.dto.RefreshResponse;
import com.codearena.codearena.entity.RefreshToken;
import com.codearena.codearena.entity.User;
import com.codearena.codearena.exception.RefreshTokenExpiredException;
import com.codearena.codearena.exception.RefreshTokenNotFoundException;
import com.codearena.codearena.repository.RefreshTokenRepository;
import com.codearena.codearena.security.GeneratedRefreshToken;
import com.codearena.codearena.security.JwtService;
import com.codearena.codearena.security.TokenHashService;

import jakarta.transaction.Transactional;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final TokenHashService tokenHashService;

    @Value("${refresh-token.expiration}")
    private Duration refreshTokenExpiration;

    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository,
            JwtService jwtService,
            TokenHashService tokenHashService) {

        this.refreshTokenRepository =
                refreshTokenRepository;
        this.jwtService = jwtService;
        this.tokenHashService = tokenHashService;
    }

   public GeneratedRefreshToken createRefreshToken(User user) {

    String rawToken =
            UUID.randomUUID().toString();

    String tokenHash =
            tokenHashService.hash(rawToken);

    RefreshToken refreshToken = new RefreshToken();

    refreshToken.setTokenHash(tokenHash);

    refreshToken.setExpiresAt(
            Instant.now().plus(
                    refreshTokenExpiration
            )
    );

    refreshToken.setUser(user);

    RefreshToken savedToken =
            refreshTokenRepository.save(
                    refreshToken
            );

    return new GeneratedRefreshToken(
            rawToken,
            savedToken
    );
}

    public RefreshToken verifyExpiration(
            RefreshToken refreshToken) {

        if (refreshToken.getExpiresAt()
                .isBefore(Instant.now())) {

           throw new RefreshTokenExpiredException(
        "Refresh token has expired"
);
        }

        return refreshToken;
    }

public RefreshToken findByToken(String rawToken) {

    String tokenHash =
            tokenHashService.hash(rawToken);

    return refreshTokenRepository
            .findByTokenHash(tokenHash)
            .orElseThrow(() ->
                    new RefreshTokenNotFoundException(
                            "Refresh token not found"
                    )
            );
}

@Transactional
public RefreshResponse refreshAccessToken(String token) {

    RefreshToken oldRefreshToken =
            findByToken(token);

    verifyExpiration(oldRefreshToken);

    User user =
            oldRefreshToken.getUser();

    refreshTokenRepository.delete(
            oldRefreshToken
    );

    GeneratedRefreshToken newRefreshToken =
            createRefreshToken(user);

    String newAccessToken =
            jwtService.generateToken(user);

    return new RefreshResponse(
            newAccessToken,
            newRefreshToken.getRawToken()
    );
}

public void deleteByToken(String token) {

    String tokenHash =
            tokenHashService.hash(token);

    refreshTokenRepository
            .findByTokenHash(tokenHash)
            .ifPresent(
                    refreshTokenRepository::delete
            );
}
public void deleteByUserId(Long userId) {

    refreshTokenRepository.deleteByUserId(userId);
}
}