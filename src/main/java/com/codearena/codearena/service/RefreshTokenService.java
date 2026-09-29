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
import com.codearena.codearena.security.GeneratedRefreshToken;
import com.codearena.codearena.security.JwtService;
import com.codearena.codearena.security.TokenHashService;

import jakarta.transaction.Transactional;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final TokenHashService tokenHashService;

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

    RefreshToken refreshToken =
            new RefreshToken();

    refreshToken.setTokenHash(tokenHash);

    refreshToken.setExpiresAt(
            Instant.now().plusSeconds(
                    7 * 24 * 60 * 60
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
@Transactional 
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

    GeneratedRefreshToken generatedRefreshToken =
        refreshTokenService.createRefreshToken(user);

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