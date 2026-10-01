package com.codearena.codearena.controller;

import com.codearena.codearena.dto.ProblemTestCaseRequest;
import com.codearena.codearena.dto.ProblemTestCaseResponse;
import com.codearena.codearena.service.ProblemTestCaseService;

import jakarta.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
public ProblemTestCaseResponse createTestCase(

        @PathVariable Long problemId,

        @Valid
        @RequestBody
        ProblemTestCaseRequest request) {

    return problemTestCaseService.createTestCase(
            problemId,
            request
    );
}

    @GetMapping
    public List<ProblemTestCaseResponse> getTestCases(
            @PathVariable Long problemId) {
        return problemTestCaseService.getTestCases(problemId);
    }

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

    @GetMapping("/samples")
public List<ProblemTestCaseResponse> getSampleTestCases(
        @PathVariable Long problemId) {

    return problemTestCaseService
            .getSampleTestCases(problemId);
}
}