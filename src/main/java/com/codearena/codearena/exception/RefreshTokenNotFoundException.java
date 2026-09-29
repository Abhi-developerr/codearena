package com.codearena.codearena.exception;

public class RefreshTokenNotFoundException
        extends RuntimeException {

    public RefreshTokenNotFoundException(
            String message) {

        super(message);
    }
}