package com.codearena.codearena.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class ProblemTestCaseResponse {

    @Schema(
        description = "Test case ID",
        example = "1"
    )
    private Long id;

    @Schema(
        description = "Test case input",
        example = "[2,7,11,15],9"
    )
    private String input;

    @Schema(
        description = "Expected output for the test case",
        example = "[0,1]"
    )
    private String expectedOutput;

    @Schema(
        description = "Whether this test case is hidden from users",
        example = "true"
    )
    private boolean hidden;

    public ProblemTestCaseResponse(
            Long id,
            String input,
            String expectedOutput,
            boolean hidden) {

        this.id = id;
        this.input = input;
        this.expectedOutput = expectedOutput;
        this.hidden = hidden;
    }

    public Long getId() {
        return id;
    }

    public String getInput() {
        return input;
    }

    public String getExpectedOutput() {
        return expectedOutput;
    }

    public boolean isHidden() {
        return hidden;
    }
}