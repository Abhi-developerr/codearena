package com.codearena.codearena.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class RefreshResponse {

    @Schema(
        description = "New short-lived JWT access token",
        example = "eyJhbGciOiJIUzI1NiJ9..."
)
private final String accessToken;

    @Schema(
        description = "New refresh token generated through refresh token rotation",
        example = "550e8400-e29b-41d4-a716-446655440000"
)
private final String refreshToken;

    public RefreshResponse(
            String accessToken,
            String refreshToken) {

        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
