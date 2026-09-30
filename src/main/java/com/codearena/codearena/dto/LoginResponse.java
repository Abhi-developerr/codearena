package com.codearena.codearena.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class LoginResponse {

    @Schema(
            description = "Unique user ID",
            example = "1"
    )
    private Long id;

    @Schema(
            description = "User's display name",
            example = "Abhishek"
    )
    private String name;

    @Schema(
            description = "User's registered email",
            example = "abhishek@example.com"
    )
    private String email;

    @Schema(
            description = "Short-lived JWT access token used to access protected APIs",
            example = "eyJhbGciOiJIUzI1NiJ9..."
    )
    private String accessToken;

    @Schema(
            description = "Long-lived refresh token used to obtain a new access token",
            example = "550e8400-e29b-41d4-a716-446655440000"
    )
    private String refreshToken;

    public LoginResponse(
            Long id,
            String name,
            String email,
            String accessToken,
            String refreshToken) {

        this.id = id;
        this.name = name;
        this.email = email;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
