package com.codearena.codearena.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codearena.codearena.entity.RefreshToken;

public interface RefreshTokenRepository
        extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    void deleteByUserId(Long userId);
}