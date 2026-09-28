package com.codearena.codearena.dto;

public class LoginResponse {

    private Long id;
    private String name;
    private String email;
    private String token;
    private String refreshToken;

   public LoginResponse(
        Long id,
        String name,
        String email,
        String token,
        String refreshToken) {

    this.id = id;
    this.name = name;
    this.email = email;
    this.token = token;
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

    public String getToken() {
    return token;
}

    public String getRefreshToken() {
        return refreshToken;
    }
}