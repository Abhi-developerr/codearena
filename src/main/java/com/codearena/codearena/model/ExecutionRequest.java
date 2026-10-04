package com.codearena.codearena.model;

import com.codearena.codearena.entity.SubmissionLanguage;

public class ExecutionRequest {

    private Long submissionId;

    private SubmissionLanguage language;

    private String sourceCode;

    public ExecutionRequest() {
    }

    public ExecutionRequest(
            Long submissionId,
            SubmissionLanguage language,
            String sourceCode) {

        this.submissionId = submissionId;
        this.language = language;
        this.sourceCode = sourceCode;
    }

    public Long getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(Long submissionId) {
        this.submissionId = submissionId;
    }

    public SubmissionLanguage getLanguage() {
        return language;
    }

    public void setLanguage(
            SubmissionLanguage language) {

        this.language = language;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }
}