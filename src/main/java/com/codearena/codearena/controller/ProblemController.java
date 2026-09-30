package com.codearena.codearena.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

import com.codearena.codearena.dto.ProblemRequest;
import com.codearena.codearena.dto.ProblemResponse;
import com.codearena.codearena.entity.Difficulty;
import com.codearena.codearena.service.ProblemService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/problems")
@Tag(
        name = "Problems",
        description = "Coding problem management APIs"
)
@Validated
public class ProblemController {

    private final ProblemService problemService;

    // Constructor Injection
    public ProblemController(ProblemService problemService) {
        this.problemService = problemService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
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

    @GetMapping
    @Operation(
            summary = "Get all problems",
            description = "Returns all available coding problems."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Problems retrieved successfully",
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
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    public Page<ProblemResponse> getAllProblems(

            @RequestParam(required = false)
            Difficulty difficulty,

            @RequestParam(required = false)
            @Size(
                    max = 100,
                    message = "Search must not exceed 100 characters"
            )
            String search,

            @PageableDefault(
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable) {

        validateSort(pageable);
        validatePageable(pageable);

        return problemService.getAllProblems(
                difficulty,
                search,
                pageable
        );
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Update a problem",
            description = "Updates an existing coding problem by its ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Problem updated successfully",
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
            ),
            @ApiResponse(
                    responseCode = "404",
                    ref = "#/components/responses/NotFound"
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    public ProblemResponse updateProblem(

            @PathVariable Long id,

            @Valid @RequestBody ProblemRequest request) {

        return problemService.updateProblem(
                id,
                request
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Delete a problem",
            description = "Deletes an existing coding problem by its ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Problem deleted successfully"
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
    public ResponseEntity<Void> deleteProblem(
            @PathVariable Long id) {

        problemService.deleteProblem(id);

        return ResponseEntity
                .noContent()
                .build();
    }

 private void validateSort(Pageable pageable) {

        for (Sort.Order order : pageable.getSort()) {

            String property = order.getProperty();

            if (!property.equals("createdAt")
                    && !property.equals("title")
                    && !property.equals("difficulty")) {

                throw new IllegalArgumentException(
                        "Invalid sort field: " + property
                );
            }
        }
    }

    private void validatePageable(Pageable pageable) {

        // Page number validation
        if (pageable.getPageNumber() < 0) {

            throw new IllegalArgumentException(
                    "Page number must not be negative"
            );
        }

        // Page size validation
        if (pageable.getPageSize() < 1
                || pageable.getPageSize() > 50) {

            throw new IllegalArgumentException(
                    "Page size must be between 1 and 50"
            );
        }
    }
}