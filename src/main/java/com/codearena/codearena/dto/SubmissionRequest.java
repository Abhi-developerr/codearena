package com.codearena.codearena.dto;

import com.codearena.codearena.entity.SubmissionLanguage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class SubmissionRequest {

    @NotNull(message = "Problem ID is required")
    private Long problemId;

    @NotNull(message = "Language is required")
    private SubmissionLanguage language;

    @NotBlank(message = "Source code is required")
    @Size(
        max = 50000,
        message = "Source code must not exceed 50000 characters"
    )
private String sourceCode;

    public SubmissionRequest() {
    }

    public SubmissionRequest(
            Long problemId,
            SubmissionLanguage language,
            String sourceCode
    ) {
        this.problemId = problemId;
        this.language = language;
        this.sourceCode = sourceCode;
    }

    public Long getProblemId() {
        return problemId;
    }

    public void setProblemId(Long problemId) {
        this.problemId = problemId;
    }

    public SubmissionLanguage getLanguage() {
        return language;
    }

    public void setLanguage(SubmissionLanguage language) {
        this.language = language;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }
}