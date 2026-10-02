package com.codearena.codearena.dto;

import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

public class RefreshTokenRequest {
    @Schema(
        description = "Refresh token used to obtain a new access token",
        example = "550e8400-e29b-41d4-a716-446655440000"
)
@NotBlank(message = "Refresh token is required")
private String refreshToken;

    public RefreshTokenRequest() {
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}