package com.codearena.codearena.security;

import com.codearena.codearena.entity.RefreshToken;

public class GeneratedRefreshToken {

    private final String rawToken;
    private final RefreshToken refreshToken;

    public GeneratedRefreshToken(
            String rawToken,
            RefreshToken refreshToken) {

        this.rawToken = rawToken;
        this.refreshToken = refreshToken;
    }

    public String getRawToken() {
        return rawToken;
    }

    public RefreshToken getRefreshToken() {
        return refreshToken;
    }
}