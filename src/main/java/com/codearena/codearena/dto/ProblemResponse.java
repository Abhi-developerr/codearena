package com.codearena.codearena.dto;

import java.time.Instant;

import com.codearena.codearena.entity.Difficulty;

import io.swagger.v3.oas.annotations.media.Schema;

public class ProblemResponse {

    @Schema(
        description = "Unique problem ID",
        example = "1"
    )
    private Long id;

    @Schema(
        description = "Problem title",
        example = "Two Sum"
    )
    private String title;

    @Schema(
        description = "Problem statement",
        example = "Given an array of integers, return indices of the two numbers..."
    )
    private String description;

    @Schema(
        description = "Problem difficulty",
        example = "EASY"
    )
    private Difficulty difficulty;

    @Schema(
        description = "Problem constraints",
        example = "2 <= nums.length <= 10^4"
    )
    private String constraints;

    @Schema(
        description = "Expected input format",
        example = "nums = [2,7,11,15], target = 9"
    )
    private String inputFormat;

    @Schema(
        description = "Expected output format",
        example = "[0,1]"
    )
    private String outputFormat;

@Schema(
    description = "Time when the problem was created",
    example = "2026-09-29T18:20:00Z"
)
private Instant createdAt;

@Schema(
    description = "Time when the problem was last updated",
    example = "2026-09-29T18:30:00Z"
)
private Instant updatedAt;

    public ProblemResponse() {
    }

    public ProblemResponse(
            Long id,
            String title,
            String description,
            Difficulty difficulty,
            String constraints,
            String inputFormat,
            String outputFormat,
            Instant createdAt,
            Instant updatedAt) {

        this.id = id;
        this.title = title;
        this.description = description;
        this.difficulty = difficulty;
        this.constraints = constraints;
        this.inputFormat = inputFormat;
        this.outputFormat = outputFormat;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public String getConstraints() {
        return constraints;
    }

    public String getInputFormat() {
        return inputFormat;
    }

    public String getOutputFormat() {
        return outputFormat;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

}