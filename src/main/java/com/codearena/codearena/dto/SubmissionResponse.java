package com.codearena.codearena.dto;

import com.codearena.codearena.entity.SubmissionLanguage;
import com.codearena.codearena.entity.SubmissionStatus;
import com.codearena.codearena.entity.SubmissionVerdict;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Submission response")
public class SubmissionResponse {

    @Schema(
        description = "Submission ID",
        example = "101"
    )
    private Long id;

    @Schema(
        description = "Problem ID",
        example = "1"
    )
    private Long problemId;

    @Schema(
        description = "Programming language used for the submission",
        example = "JAVA"
    )
    private SubmissionLanguage language;

    @Schema(
        description = "Current processing status",
        example = "QUEUED"
    )
    private SubmissionStatus status;

    @Schema(
        description = "Final execution verdict",
        example = "ACCEPTED",
        nullable = true
    )
    private SubmissionVerdict verdict;

    @Schema(
        description = "Execution time in milliseconds",
        example = "125",
        nullable = true
    )
    private Long executionTime;

    @Schema(
        description = "Memory used in KB",
        example = "32768",
        nullable = true
    )
    private Long memoryUsed;

    @Schema(
        description = "Submission creation time",
        example = "2026-10-03T10:30:00Z"
    )
    private Instant createdAt;

    @Schema(
        description = "Submission last update time",
        example = "2026-10-03T10:30:02Z"
    )
    private Instant updatedAt;

    public SubmissionResponse(
            Long id,
            Long problemId,
            SubmissionLanguage language,
            SubmissionStatus status,
            SubmissionVerdict verdict,
            Long executionTime,
            Long memoryUsed,
            Instant createdAt,
            Instant updatedAt) {

        this.id = id;
        this.problemId = problemId;
        this.language = language;
        this.status = status;
        this.verdict = verdict;
        this.executionTime = executionTime;
        this.memoryUsed = memoryUsed;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getProblemId() {
        return problemId;
    }

    public SubmissionLanguage getLanguage() {
        return language;
    }

    public SubmissionStatus getStatus() {
        return status;
    }

    public SubmissionVerdict getVerdict() {
        return verdict;
    }

    public Long getExecutionTime() {
        return executionTime;
    }

    public Long getMemoryUsed() {
        return memoryUsed;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}