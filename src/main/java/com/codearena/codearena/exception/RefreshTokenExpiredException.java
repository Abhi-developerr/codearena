package com.codearena.codearena.exception;

public class RefreshTokenExpiredException
        extends RuntimeException {

    public RefreshTokenExpiredException(
            String message) {

        super(message);
    }
}