package com.codearena.codearena.model;

public class TestCaseExecutionResult {

    private Long testCaseId;

    private boolean passed;

    private Long executionTime;

    private Long memoryUsed;

    private String actualOutput;

    private String errorMessage;

    public TestCaseExecutionResult() {
    }

    public TestCaseExecutionResult(
            Long testCaseId,
            boolean passed,
            Long executionTime,
            Long memoryUsed,
            String actualOutput,
            String errorMessage) {

        this.testCaseId = testCaseId;
        this.passed = passed;
        this.executionTime = executionTime;
        this.memoryUsed = memoryUsed;
        this.actualOutput = actualOutput;
        this.errorMessage = errorMessage;
    }

    public Long getTestCaseId() {
        return testCaseId;
    }

    public void setTestCaseId(Long testCaseId) {
        this.testCaseId = testCaseId;
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
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

    public String getActualOutput() {
        return actualOutput;
    }

    public void setActualOutput(String actualOutput) {
        this.actualOutput = actualOutput;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}