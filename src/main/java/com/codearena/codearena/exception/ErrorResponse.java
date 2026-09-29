package com.codearena.codearena.exception;

import java.time.Instant;
import java.util.Map;

public class ErrorResponse {

    private final int status;
    private final String message;
    private final Map<String, String> errors;
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