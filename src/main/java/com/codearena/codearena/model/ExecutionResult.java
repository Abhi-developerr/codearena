package com.codearena.codearena.model;

import com.codearena.codearena.entity.SubmissionVerdict;

public class ExecutionResult {

    private SubmissionVerdict verdict;

    private Long executionTime;

    private Long memoryUsed;

    private String errorMessage;

    public ExecutionResult() {
    }

    public ExecutionResult(
            SubmissionVerdict verdict,
            Long executionTime,
            Long memoryUsed,
            String errorMessage) {

        this.verdict = verdict;
        this.executionTime = executionTime;
        this.memoryUsed = memoryUsed;
        this.errorMessage = errorMessage;
    }

    public SubmissionVerdict getVerdict() {
        return verdict;
    }

    public void setVerdict(SubmissionVerdict verdict) {
        this.verdict = verdict;
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

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}