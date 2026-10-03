package com.codearena.codearena.controller;

import com.codearena.codearena.dto.SubmissionRequest;
import com.codearena.codearena.dto.SubmissionResponse;
import com.codearena.codearena.security.CustomUserDetails;
import com.codearena.codearena.service.SubmissionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(
    name = "Submissions",
    description = "Code submission and execution APIs"
)
@RestController
@RequestMapping("/api/submissions")
public class SubmissionController {

    private final SubmissionService submissionService;

    public SubmissionController(
            SubmissionService submissionService) {

        this.submissionService = submissionService;
    }

    @Operation(
    summary = "Create a submission",
    description = "Submit source code for a coding problem"
)
@ApiResponses({
    @ApiResponse(
        responseCode = "201",
        description = "Submission created successfully"
    ),
    @ApiResponse(
        responseCode = "400",
        description = "Invalid submission request"
    ),
    @ApiResponse(
        responseCode = "401",
        description = "Authentication required"
    ),
    @ApiResponse(
        responseCode = "404",
        description = "Problem not found"
    )
})
@SecurityRequirement(name = "bearerAuth")
   @PostMapping
public ResponseEntity<SubmissionResponse> createSubmission(
        @Valid @RequestBody SubmissionRequest request,
        Authentication authentication) {

    CustomUserDetails userDetails =
            (CustomUserDetails) authentication.getPrincipal();

    Long userId =
            userDetails.getUser().getId();

    SubmissionResponse response =
            submissionService.createSubmission(
                    userId,
                    request
            );

    return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(response);
}

@GetMapping
@Operation(
    summary = "Get my submissions",
    description = "Returns submissions created by the authenticated user"
)
@ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Submissions retrieved successfully"
    ),
    @ApiResponse(
        responseCode = "401",
        description = "Authentication required"
    )
})
@SecurityRequirement(name = "bearerAuth")
public List<SubmissionResponse> getMySubmissions(
        Authentication authentication) {

    CustomUserDetails userDetails =
            (CustomUserDetails) authentication.getPrincipal();

    Long userId =
            userDetails.getUser().getId();

    return submissionService.getMySubmissions(
            userId
    );
}

@GetMapping("/{submissionId}")
@Operation(
    summary = "Get my submission",
    description = "Returns a submission belonging to the authenticated user"
)
@ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Submission retrieved successfully"
    ),
    @ApiResponse(
        responseCode = "401",
        description = "Authentication required"
    ),
    @ApiResponse(
        responseCode = "404",
        description = "Submission not found"
    )
})
@SecurityRequirement(name = "bearerAuth")
public SubmissionResponse getMySubmission(
        @PathVariable Long submissionId,
        Authentication authentication) {

    CustomUserDetails userDetails =
            (CustomUserDetails) authentication.getPrincipal();

    Long userId =
            userDetails.getUser().getId();

    return submissionService.getMySubmission(
            userId,
            submissionId
    );
}

}