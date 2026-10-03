package com.codearena.codearena.service;

import com.codearena.codearena.dto.ProblemTestCasePublicResponse;
import com.codearena.codearena.dto.ProblemTestCaseRequest;
import com.codearena.codearena.dto.ProblemTestCaseResponse;
import com.codearena.codearena.entity.Problem;
import com.codearena.codearena.entity.ProblemTestCase;
import com.codearena.codearena.exception.ProblemTestCaseNotFoundException;
import com.codearena.codearena.exception.ProblemTestCaseOwnershipException;
import com.codearena.codearena.exception.ProblemNotFoundException;
import com.codearena.codearena.repository.ProblemRepository;
import com.codearena.codearena.repository.ProblemTestCaseRepository;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProblemTestCaseService {

    private final ProblemTestCaseRepository problemTestCaseRepository;

    private final ProblemRepository problemRepository;

    public ProblemTestCaseService(
            ProblemTestCaseRepository problemTestCaseRepository,
            ProblemRepository problemRepository) {

        this.problemTestCaseRepository = problemTestCaseRepository;

        this.problemRepository = problemRepository;
    }

    @Transactional
    public ProblemTestCaseResponse createTestCase(Long problemId, ProblemTestCaseRequest request) {

    Problem problem = problemRepository.findById(problemId)
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

    ProblemTestCase savedTestCase = problemTestCaseRepository.save(testCase);

    return toAdminResponse(savedTestCase);
}

    private ProblemTestCaseResponse toAdminResponse(
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
            .map(this::toAdminResponse)
            .collect(Collectors.toList());
}

public List<ProblemTestCasePublicResponse> getSampleTestCases(
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
            .map(this::toPublicResponse)
            .collect(Collectors.toList());
}

public ProblemTestCaseResponse updateTestCase(
        Long problemId,
        Long testCaseId,
        ProblemTestCaseRequest request) {

    Problem problem =
            problemRepository.findById(problemId)
                    .orElseThrow(() ->
                            new ProblemNotFoundException(
                                    "Problem not found"
                            )
                    );

    ProblemTestCase testCase =
            findTestCaseForProblem(problemId, testCaseId);


    if (!testCase.getProblem().getId().equals(problem.getId())) {
        throw new IllegalArgumentException(
                "Test case does not belong to this problem"
        );
    }

    testCase.setInput(
            request.getInput().trim()
    );

    testCase.setExpectedOutput(
            request.getExpectedOutput().trim()
    );

    testCase.setHidden(
            request.isHidden()
    );

    ProblemTestCase updatedTestCase =
            problemTestCaseRepository.save(testCase);

    return toAdminResponse(updatedTestCase);
}

@Transactional
public void deleteTestCase(
        Long problemId,
        Long testCaseId) {

    Problem problem =
            problemRepository.findById(problemId)
                    .orElseThrow(() ->
                            new ProblemNotFoundException(
                                    "Problem not found"
                            )
                    );

    ProblemTestCase testCase =
            problemTestCaseRepository.findById(testCaseId)
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Test case not found"
                            )
                    );

    if (!testCase.getProblem().getId().equals(problem.getId())) {
        throw new IllegalArgumentException(
                "Test case does not belong to this problem"
        );
    }

    problemTestCaseRepository.delete(testCase);
}

private ProblemTestCase findTestCaseForProblem(
        Long problemId,
        Long testCaseId) {

    Problem problem =
            problemRepository.findById(problemId)
                    .orElseThrow(() ->
                            new ProblemNotFoundException(
                                    "Problem not found"
                            )
                    );

    ProblemTestCase testCase =
            problemTestCaseRepository.findById(testCaseId)
                    .orElseThrow(() ->
                            new ProblemTestCaseNotFoundException(
                                    "Test case not found"
                            )
                    );

    if (!testCase.getProblem().getId()
            .equals(problem.getId())) {

        throw new ProblemTestCaseOwnershipException(
                "Test case does not belong to this problem"
        );
    }

    return testCase;
}

private ProblemTestCasePublicResponse toPublicResponse(
        ProblemTestCase testCase) {

    return new ProblemTestCasePublicResponse(
            testCase.getId(),
            testCase.getInput(),
            testCase.getExpectedOutput()
    );
}

}