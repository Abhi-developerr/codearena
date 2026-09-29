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
    private String description;

    @NotNull(message = "Difficulty is required")
    private Difficulty difficulty;

    private String constraints;

    private String inputFormat;

    private String outputFormat;

    public ProblemRequest() {
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

    public void setConstraints(String constraints) {
        this.constraints = constraints;
    }

    public void setInputFormat(String inputFormat) {
        this.inputFormat = inputFormat;
    }

    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }
}