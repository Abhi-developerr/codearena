package com.codearena.codearena.dto;

public class RefreshResponse {

    private final String accessToken;
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
