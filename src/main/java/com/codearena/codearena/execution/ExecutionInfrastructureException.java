
package com.codearena.codearena.execution;

public class ExecutionInfrastructureException extends RuntimeException {

    public ExecutionInfrastructureException(String message) {
        super(message);
    }

    public ExecutionInfrastructureException(
            String message,
            Throwable cause) {
        super(message, cause);
    }
}
