package com.codearena.codearena.security;

public class JwtClaims {

    private final String email;
    private final long tokenVersion;

    public JwtClaims(
            String email,
            long tokenVersion) {

        this.email = email;
        this.tokenVersion = tokenVersion;
    }

    public String getEmail() {
        return email;
    }

    public long getTokenVersion() {
        return tokenVersion;
    }
}