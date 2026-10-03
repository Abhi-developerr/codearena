package com.codearena.codearena.controller;

import com.codearena.codearena.dto.ProblemTestCaseRequest;
import com.codearena.codearena.dto.ProblemTestCasePublicResponse;
import com.codearena.codearena.dto.ProblemTestCaseResponse;
import com.codearena.codearena.service.ProblemTestCaseService;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(
    name = "Problem Test Cases",
    description = "Problem test case management APIs"
)
@RestController
@RequestMapping("/api/problems/{problemId}/test-cases")
public class ProblemTestCaseController {

    private final ProblemTestCaseService
            problemTestCaseService;

    public ProblemTestCaseController(
            ProblemTestCaseService problemTestCaseService) {

        this.problemTestCaseService =
                problemTestCaseService;
    }

    @Operation(
    summary = "Create a test case",
    description = "Creates a test case for a problem. Admin only."
)
@ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Test case created successfully"
    ),
    @ApiResponse(
        responseCode = "400",
        ref = "#/components/responses/BadRequest"
    ),
    @ApiResponse(
        responseCode = "401",
        ref = "#/components/responses/Unauthorized"
    ),
    @ApiResponse(
        responseCode = "403",
        ref = "#/components/responses/Forbidden"
    ),
    @ApiResponse(
        responseCode = "404",
        ref = "#/components/responses/NotFound"
    )
})
@SecurityRequirement(name = "bearerAuth")
   @PostMapping
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<ProblemTestCaseResponse> createTestCase(
        @PathVariable Long problemId,
        @Valid @RequestBody ProblemTestCaseRequest request) {

    ProblemTestCaseResponse response =
            problemTestCaseService.createTestCase(
                    problemId,
                    request
            );

    return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(response);
}

@Operation(
    summary = "Delete a test case",
    description = "Deletes an existing test case. Admin only."
)
@ApiResponses({
    @ApiResponse(
        responseCode = "204",
        description = "Test case deleted successfully"
    ),
    @ApiResponse(
        responseCode = "401",
        ref = "#/components/responses/Unauthorized"
    ),
    @ApiResponse(
        responseCode = "403",
        ref = "#/components/responses/Forbidden"
    ),
    @ApiResponse(
        responseCode = "404",
        ref = "#/components/responses/NotFound"
    )
})
@SecurityRequirement(name = "bearerAuth")
@DeleteMapping("/{testCaseId}")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<Void> deleteTestCase(

        @PathVariable Long problemId,

        @PathVariable Long testCaseId) {

    problemTestCaseService.deleteTestCase(
            problemId,
            testCaseId
    );

    return ResponseEntity
            .noContent()
            .build();
}

@Operation(
    summary = "Get all test cases",
    description = "Returns all test cases for a problem. Admin only."
)
@ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Test cases retrieved successfully"
    ),
    @ApiResponse(
        responseCode = "401",
        ref = "#/components/responses/Unauthorized"
    ),
    @ApiResponse(
        responseCode = "403",
        ref = "#/components/responses/Forbidden"
    ),
    @ApiResponse(
        responseCode = "404",
        ref = "#/components/responses/NotFound"
    )
})
@SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public List<ProblemTestCaseResponse> getTestCases(
            @PathVariable Long problemId) {
        return problemTestCaseService.getTestCases(problemId);
    }

    @Operation(
    summary = "Update a test case",
    description = "Updates an existing test case. Admin only."
)
@ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Test case updated successfully"
    ),
    @ApiResponse(
        responseCode = "400",
        ref = "#/components/responses/BadRequest"
    ),
    @ApiResponse(
        responseCode = "401",
        ref = "#/components/responses/Unauthorized"
    ),
    @ApiResponse(
        responseCode = "403",
        ref = "#/components/responses/Forbidden"
    ),
    @ApiResponse(
        responseCode = "404",
        ref = "#/components/responses/NotFound"
    )
})
@SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{testCaseId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ProblemTestCaseResponse updateTestCase(
            @PathVariable Long problemId,
            @PathVariable Long testCaseId,
            @Valid
            @RequestBody
            ProblemTestCaseRequest request) {
        return problemTestCaseService.updateTestCase(
                problemId,
                testCaseId,
                request
        );
    }

    @Operation(
    summary = "Get sample test cases",
    description = "Returns only non-hidden sample test cases."
)
@ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Sample test cases retrieved successfully"
    ),
    @ApiResponse(
        responseCode = "401",
        ref = "#/components/responses/Unauthorized"
    ),
    @ApiResponse(
        responseCode = "404",
        ref = "#/components/responses/NotFound"
    )
})
@SecurityRequirement(name = "bearerAuth")
    @GetMapping("/samples")
public List<ProblemTestCasePublicResponse> getSampleTestCases(
        @PathVariable Long problemId) {

    return problemTestCaseService
            .getSampleTestCases(problemId);
}
}