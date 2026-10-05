package com.codearena.codearena.model;

import com.codearena.codearena.model.SandboxErrorType;

public class SandboxExecutionResult {

    private boolean success;
    private String output;
    private String errorMessage;
    private Long executionTime;
    private Long memoryUsed;
    private SandboxErrorType errorType;

    public SandboxExecutionResult() {
    }

    public SandboxExecutionResult(
            boolean success,
            String output,
            String errorMessage,
            Long executionTime,
            Long memoryUsed,
            SandboxErrorType errorType) {

        this.success = success;
        this.output = output;
        this.errorMessage = errorMessage;
        this.executionTime = executionTime;
        this.memoryUsed = memoryUsed;
        this.errorType = errorType;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Long getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(Long executionTime) {
        this.executionTime = executionTime;
    }

    public Long getMemoryUsed() {
        return memoryUsed;
    }

    public void setMemoryUsed(Long memoryUsed) {
        this.memoryUsed = memoryUsed;
    }

    public SandboxErrorType getErrorType() {
        return errorType;
    }
 
    public void setErrorType(SandboxErrorType errorType) {
    this.errorType = errorType;
}

}