package com.codearena.codearena.service;

import com.codearena.codearena.dto.ProblemTestCaseRequest;
import com.codearena.codearena.dto.ProblemTestCaseResponse;
import com.codearena.codearena.entity.Problem;
import com.codearena.codearena.entity.ProblemTestCase;
import com.codearena.codearena.exception.ProblemNotFoundException;
import com.codearena.codearena.repository.ProblemRepository;
import com.codearena.codearena.repository.ProblemTestCaseRepository;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class ProblemTestCaseService {

    private final ProblemTestCaseRepository
            problemTestCaseRepository;

    private final ProblemRepository problemRepository;

    public ProblemTestCaseService(
            ProblemTestCaseRepository problemTestCaseRepository,
            ProblemRepository problemRepository) {

        this.problemTestCaseRepository =
                problemTestCaseRepository;

        this.problemRepository =
                problemRepository;
    }

    public ProblemTestCaseResponse createTestCase(
        Long problemId,
        ProblemTestCaseRequest request) {

    Problem problem =
            problemRepository.findById(problemId)
                    .orElseThrow(() ->
                            new ProblemNotFoundException(
                                    "Problem not found"
                            )
                    );

    ProblemTestCase testCase =
            new ProblemTestCase();

    testCase.setInput(
            request.getInput().trim()
    );

    testCase.setExpectedOutput(
            request.getExpectedOutput().trim()
    );

    testCase.setHidden(
            request.isHidden()
    );

    testCase.setProblem(problem);

    ProblemTestCase savedTestCase =
            problemTestCaseRepository.save(testCase);

    return toResponse(savedTestCase);
}

    private ProblemTestCaseResponse toResponse(
            ProblemTestCase testCase) {

        return new ProblemTestCaseResponse(
                testCase.getId(),
                testCase.getInput(),
                testCase.getExpectedOutput(),
                testCase.isHidden()
        );
    }

    public List<ProblemTestCaseResponse> getTestCases(
        Long problemId) {

    problemRepository.findById(problemId)
            .orElseThrow(() ->
                    new ProblemNotFoundException(
                            "Problem not found"
                    )
            );

    return problemTestCaseRepository
            .findByProblemId(problemId)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
}

public List<ProblemTestCaseResponse> getSampleTestCases(
        Long problemId) {

    problemRepository.findById(problemId)
            .orElseThrow(() ->
                    new ProblemNotFoundException(
                            "Problem not found"
                    )
            );

    return problemTestCaseRepository
            .findByProblemIdAndHiddenFalse(problemId)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
}

}