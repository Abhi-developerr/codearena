package com.codearena.codearena.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Submission source code response")
public class SubmissionCodeResponse {

    @Schema(
        description = "Submission ID",
        example = "25"
    )
    private final Long submissionId;

    @Schema(
        description = "Source code submitted by the user",
        example = "class Solution { ... }"
    )
    private final String sourceCode;

    public SubmissionCodeResponse(
            Long submissionId,
            String sourceCode) {

        this.submissionId = submissionId;
        this.sourceCode = sourceCode;
    }

    public Long getSubmissionId() {
        return submissionId;
    }

    public String getSourceCode() {
        return sourceCode;
    }
}