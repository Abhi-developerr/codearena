package com.codearena.codearena.dto;
public class LoginResponse {

    private final Long id;
    private final String name;
    private final String email;

    private final String accessToken;
    private final String refreshToken;

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