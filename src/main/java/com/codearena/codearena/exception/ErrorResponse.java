package com.codearena.codearena.exception;

import java.time.Instant;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description = "Standard API error response"
)
public class ErrorResponse {

@Schema(
        description = "HTTP status code",
        example = "400"
)
private int status;

@Schema(
        description = "Human-readable error message",
        example = "Invalid request"
)
private String message;

@Schema(
        description = "Field-level validation errors",
        nullable = true
)
private final Map<String, String> errors;

@Schema(
        description = "Time when the error occurred",
        example = "2026-09-29T18:30:00Z"
)
private final Instant timestamp;

    public ErrorResponse(
            int status,
            String message,
            Map<String, String> errors) {

        this.status = status;
        this.message = message;
        this.errors = errors;
        this.timestamp = Instant.now();
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public Instant getTimestamp() {
    return timestamp;
}
}