package com.codearena.codearena.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Public test case response")
public class ProblemTestCasePublicResponse {

    @Schema(
        description = "Test case ID",
        example = "1"
    )
    private Long id;

    @Schema(
        description = "Sample test case input",
        example = "[2,7,11,15],9"
    )
    private String input;

    @Schema(
        description = "Expected output for the sample test case",
        example = "[0,1]"
    )
    private String expectedOutput;

    public ProblemTestCasePublicResponse(
            Long id,
            String input,
            String expectedOutput) {

        this.id = id;
        this.input = input;
        this.expectedOutput = expectedOutput;
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
}