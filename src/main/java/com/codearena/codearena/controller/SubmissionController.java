package com.codearena.codearena.controller;

import com.codearena.codearena.dto.SubmissionRequest;
import com.codearena.codearena.dto.SubmissionResponse;
import com.codearena.codearena.service.SubmissionService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/submissions")
public class SubmissionController {

    private final SubmissionService submissionService;

    public SubmissionController(
            SubmissionService submissionService) {

        this.submissionService = submissionService;
    }

    @PostMapping
    public ResponseEntity<SubmissionResponse> createSubmission(
            @Valid @RequestBody SubmissionRequest request,
            Authentication authentication) {

        Long userId =
                Long.valueOf(authentication.getName());

        SubmissionResponse response =
                submissionService.createSubmission(
                        userId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}