package com.codearena.codearena.execution;

import com.codearena.codearena.repository.ProblemTestCaseRepository;
import com.codearena.codearena.repository.SubmissionRepository;
import com.codearena.codearena.service.SubmissionService;

import java.util.ArrayList;
import com.codearena.codearena.entity.SubmissionVerdict;
import com.codearena.codearena.model.ExecutionResult;
import java.util.List;
import com.codearena.codearena.entity.Submission;
import com.codearena.codearena.entity.ProblemTestCase;
import com.codearena.codearena.exception.SubmissionNotFoundException;
import com.codearena.codearena.model.ExecutionRequest;
import com.codearena.codearena.model.TestCaseExecutionResult;


import org.springframework.stereotype.Service;

@Service
public class ExecutionService {

    private final ProblemTestCaseRepository problemTestCaseRepository;
    private final SubmissionRepository submissionRepository;
    private final TestCaseRunner testCaseRunner;
    private final SubmissionService submissionService;

    public ExecutionService(
            ProblemTestCaseRepository problemTestCaseRepository,
            SubmissionRepository submissionRepository,
            TestCaseRunner testCaseRunner,
            SubmissionService submissionService) {

        this.problemTestCaseRepository = problemTestCaseRepository;
        this.submissionRepository = submissionRepository;
        this.testCaseRunner = testCaseRunner;
        this.submissionService = submissionService;
    }

    public ExecutionResult execute(
        ExecutionRequest request,
        List<ProblemTestCase> testCases) {

    List<TestCaseExecutionResult> results =
            runAllTestCases(request, testCases);

    SubmissionVerdict verdict =
            determineVerdict(results);

    return new ExecutionResult(
            verdict,
            null,
            null,
            null,
            results
    );
}

    public ExecutionRequest createExecutionRequest(Long submissionId) {

    Submission submission = submissionRepository.findById(submissionId)
            .orElseThrow(() ->
                    new SubmissionNotFoundException(
                            "Submission not found"
                    )
            );

    return new ExecutionRequest(
            submission.getId(),
            submission.getLanguage(),
            submission.getSourceCode()
    );
}

public TestCaseExecutionResult runTestCase(
        ExecutionRequest request,
        ProblemTestCase testCase) {

    return testCaseRunner.run(request, testCase);
}

public List<TestCaseExecutionResult> runAllTestCases(
        ExecutionRequest request,
        List<ProblemTestCase> testCases) {

    List<TestCaseExecutionResult> results = new ArrayList<>();

    for (ProblemTestCase testCase : testCases) {

        TestCaseExecutionResult result =
                runTestCase(request, testCase);

        results.add(result);

        if (!result.isPassed()) {
            break;
        }
    }

    return results;
}

private SubmissionVerdict determineVerdict(
        List<TestCaseExecutionResult> results) {

    if (results.isEmpty()) {
        return SubmissionVerdict.RUNTIME_ERROR;
    }

    for (TestCaseExecutionResult result : results) {

        if (!result.isPassed()) {
            return SubmissionVerdict.WRONG_ANSWER;
        }
    }

    return SubmissionVerdict.ACCEPTED;
}

public ExecutionResult executeSubmission(Long submissionId) {

    submissionService.markAsRunning(submissionId);

    Submission submission = submissionRepository.findById(submissionId)
            .orElseThrow(() ->
                    new SubmissionNotFoundException(
                            "Submission not found"
                    )
            );

    ExecutionRequest request = new ExecutionRequest(
            submission.getId(),
            submission.getLanguage(),
            submission.getSourceCode()
    );

    List<ProblemTestCase> testCases =
            getTestCases(submission.getProblem().getId());

    return execute(request, testCases);
}

}

  