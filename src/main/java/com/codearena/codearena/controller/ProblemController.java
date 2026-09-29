package com.codearena.codearena.controller;

import com.codearena.codearena.dto.ProblemRequest;
import com.codearena.codearena.dto.ProblemResponse;
import com.codearena.codearena.service.ProblemService;
import com.codearena.codearena.exception.ErrorResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/problems")
@Tag(
    name = "Problems",
    description = "Coding problem management APIs"
)
public class ProblemController {

    private final ProblemService problemService;

    public ProblemController(
            ProblemService problemService) {
        this.problemService = problemService;
    }

   @PostMapping
@Operation(
    summary = "Create a new problem",
    description = "Creates a new coding problem."
)
@ApiResponses({
    @ApiResponse(
        responseCode = "201",
        description = "Problem created successfully",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(
                implementation = ProblemResponse.class
            )
        )
    ),
    @ApiResponse(
        responseCode = "400",
        ref = "#/components/responses/BadRequest"
    ),
    @ApiResponse(
        responseCode = "401",
        ref = "#/components/responses/Unauthorized"
    )
})
@SecurityRequirement(name = "bearerAuth")
public ResponseEntity<ProblemResponse> createProblem(
        @Valid @RequestBody ProblemRequest request) {

    ProblemResponse response =
            problemService.createProblem(request);

    return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(response);
}

@GetMapping("/{id}")
@Operation(
    summary = "Get problem by ID",
    description = "Returns a coding problem by its ID."
)
@ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Problem retrieved successfully",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(
                implementation = ProblemResponse.class
            )
        )
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
public ProblemResponse getProblemById(
        @PathVariable Long id) {

    return problemService.getProblemById(id);
}
}