package com.codearena.codearena.execution;

public class ExecutionAttemptLostException extends RuntimeException {

    public ExecutionAttemptLostException(String message) {
        super(message);
    }
}