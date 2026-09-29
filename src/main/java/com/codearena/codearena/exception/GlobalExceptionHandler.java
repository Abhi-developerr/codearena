package com.codearena.codearena.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
   @ExceptionHandler(EmailAlreadyExistsException.class)
public ErrorResponse handleEmailAlreadyExists(
        EmailAlreadyExistsException exception) {

    return new ErrorResponse(
            409,
            exception.getMessage(),
            null
    );
}

@ExceptionHandler(UserNotFoundException.class)
public ErrorResponse handleUserNotFound(
        UserNotFoundException exception) {

    return new ErrorResponse(
            401,
            exception.getMessage(),
            null
    );
}

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleValidationException(
            MethodArgumentNotValidException exception) {

        Map<String, String> errors = new HashMap<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );

        return new ErrorResponse(
                400,
                "Validation failed",
                errors
        );
    }

    @ExceptionHandler(RefreshTokenNotFoundException.class)
public ErrorResponse handleRefreshTokenNotFound(
        RefreshTokenNotFoundException exception) {

    return new ErrorResponse(
            401,
            exception.getMessage(),
            null
    );
}

@ExceptionHandler(RefreshTokenExpiredException.class)
public ErrorResponse handleRefreshTokenExpired(
        RefreshTokenExpiredException exception) {

    return new ErrorResponse(
            401,
            exception.getMessage(),
            null
    );
}
}