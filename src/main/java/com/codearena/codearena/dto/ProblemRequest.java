package com.codearena.codearena.dto;

import com.codearena.codearena.entity.Difficulty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ProblemRequest {

    @NotBlank(message = "Title is required")
    @Size(
        max = 200,
        message = "Title must not exceed 200 characters"
    )
    private String title;

    @NotBlank(message = "Description is required")
    @Size(
        max = 10000,
        message = "Description must not exceed 10000 characters"
    )
    private String description;

    @NotNull(message = "Difficulty is required")
    private Difficulty difficulty;

    @Size(
        max = 5000,
        message = "Constraints must not exceed 5000 characters"
    )
    private String constraints;

    @Size(
        max = 5000,
        message = "Input format must not exceed 5000 characters"
    )
    private String inputFormat;

    @Size(
        max = 5000,
        message = "Output format must not exceed 5000 characters"
    )
    private String outputFormat;

    public ProblemRequest() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public String getConstraints() {
        return constraints;
    }

    public void setConstraints(String constraints) {
        this.constraints = constraints;
    }

    public String getInputFormat() {
        return inputFormat;
    }

    public void setInputFormat(String inputFormat) {
        this.inputFormat = inputFormat;
    }

    public String getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }
}