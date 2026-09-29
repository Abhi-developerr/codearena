package com.codearena.codearena.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExists(
        EmailAlreadyExistsException exception) {

        ErrorResponse error = new ErrorResponse(
                409,
                exception.getMessage(),
                null
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error);
    }

   @ExceptionHandler(UserNotFoundException.class)
public ResponseEntity<ErrorResponse> handleUserNotFound(
        UserNotFoundException exception) {

    ErrorResponse error =
            new ErrorResponse(
                    404,
                    exception.getMessage(),
                    null
            );

    return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(error);
}

    @ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<ErrorResponse> handleValidationException(
        MethodArgumentNotValidException exception) {

    Map<String, String> errors =
            new HashMap<>();

    exception.getBindingResult()
            .getFieldErrors()
            .forEach(error ->
                    errors.put(
                            error.getField(),
                            error.getDefaultMessage()
                    )
            );

    ErrorResponse error =
            new ErrorResponse(
                    400,
                    "Validation failed",
                    errors
            );

    return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(error);
}

    @ExceptionHandler(RefreshTokenNotFoundException.class)
public ResponseEntity<ErrorResponse> handleRefreshTokenNotFound(
        RefreshTokenNotFoundException exception) {

    ErrorResponse error = new ErrorResponse(
            401,
            exception.getMessage(),
            null
    );

    return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(error);
}

@ExceptionHandler(RefreshTokenExpiredException.class)
public ResponseEntity<ErrorResponse> handleRefreshTokenExpired(
        RefreshTokenExpiredException exception) {

    ErrorResponse error =
            new ErrorResponse(
                    401,
                    "Invalid refresh token",
                    null
            );

    return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(error);
}

@ExceptionHandler(InvalidPasswordException.class)
public ResponseEntity<ErrorResponse> handleInvalidPassword(
        InvalidPasswordException exception) {

    ErrorResponse error = new ErrorResponse(
            400,
            exception.getMessage(),
            null
    );

    return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(error);
}

@ExceptionHandler(Exception.class)
public ResponseEntity<ErrorResponse> handleGenericException(
        Exception exception) {

    ErrorResponse error = new ErrorResponse(
            500,
            "Internal server error",
            null
    );

    return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(error);
}

@ExceptionHandler(InvalidCredentialsException.class)
public ResponseEntity<ErrorResponse> handleInvalidCredentials(
        InvalidCredentialsException exception) {

    ErrorResponse error =
            new ErrorResponse(
                    401,
                    exception.getMessage(),
                    null
            );

    return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(error);
}
}