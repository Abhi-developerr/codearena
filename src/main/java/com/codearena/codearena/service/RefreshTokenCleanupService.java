package com.codearena.codearena.service;

import com.codearena.codearena.repository.RefreshTokenRepository;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;

import jakarta.transaction.Transactional;

@Service
public class RefreshTokenCleanupService {

    private final RefreshTokenRepository refreshTokenRepository;
    public RefreshTokenCleanupService(
            RefreshTokenRepository refreshTokenRepository) {

        this.refreshTokenRepository =
                refreshTokenRepository;
    }

        @Scheduled(fixedRateString = "${refresh-token.cleanup-interval}")
    @Transactional
    public void deleteExpiredTokens() {

        refreshTokenRepository
                .deleteByExpiresAtBefore(
                        Instant.now()
                );
    }
}
