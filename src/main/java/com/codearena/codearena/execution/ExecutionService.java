package com.codearena.codearena.execution;

import com.codearena.codearena.entity.ProblemTestCase;
import com.codearena.codearena.entity.Submission;
import com.codearena.codearena.entity.SubmissionStatus;
import com.codearena.codearena.entity.SubmissionVerdict;
import com.codearena.codearena.exception.SubmissionNotFoundException;
import com.codearena.codearena.model.ExecutionRequest;
import com.codearena.codearena.model.ExecutionResult;
import com.codearena.codearena.model.SandboxErrorType;
import com.codearena.codearena.model.TestCaseExecutionResult;
import com.codearena.codearena.repository.ProblemTestCaseRepository;
import com.codearena.codearena.repository.SubmissionRepository;
import com.codearena.codearena.service.SubmissionService;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

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

        if (testCases == null || testCases.isEmpty()) {
            throw new IllegalArgumentException(
                    "Problem has no test cases"
            );
        }

        List<TestCaseExecutionResult> results =
                runAllTestCases(request, testCases);

        SubmissionVerdict verdict = determineVerdict(results);

        long totalExecutionTime =
                calculateTotalExecutionTime(results);

        long maxMemoryUsed =
                calculateMaxMemoryUsed(results);

        String errorMessage = findErrorMessage(results);

        return new ExecutionResult(
                verdict,
                totalExecutionTime,
                maxMemoryUsed,
                errorMessage,
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

    private List<ProblemTestCase> getTestCases(Long problemId) {
        return problemTestCaseRepository.findByProblemId(problemId);
    }

    public ExecutionResult executeSubmission(Long submissionId) {

        try {
            submissionService.markAsRunning(submissionId);
        } catch (IllegalArgumentException exception) {
            throw new SubmissionClaimException(
                    "Submission could not be claimed for execution",
                    exception
            );
        }

        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() ->
                        new SubmissionNotFoundException(
                                "Submission not found"
                        )
                );
                Integer attemptRetryCount = submission.getRetryCount();

        ExecutionRequest request = new ExecutionRequest(
                submission.getId(),
                submission.getLanguage(),
                submission.getSourceCode()
        );

        List<ProblemTestCase> testCases =
                getTestCases(submission.getProblem().getId());

        if (testCases == null || testCases.isEmpty()) {

            ExecutionResult result = new ExecutionResult(
                    SubmissionVerdict.RUNTIME_ERROR,
                    0L,
                    0L,
                    "Problem has no test cases configured",
                    List.of()
            );

        submissionService.updateExecutionResult(
        submissionId,
        attemptRetryCount,
        SubmissionStatus.COMPLETED,
        result.getVerdict(),
        result.getExecutionTime(),
        result.getMemoryUsed()
);

            return result;
        }

        ExecutionResult result = execute(
                request,
                testCases,
                attemptRetryCount
        );

        submissionService.updateExecutionResult(
                submissionId,
                attemptRetryCount,
                SubmissionStatus.COMPLETED,
                result.getVerdict(),
                result.getExecutionTime(),
                result.getMemoryUsed()
        );

        return result;
    }

    public List<TestCaseExecutionResult> runAllTestCases(
            ExecutionRequest request,
            List<ProblemTestCase> testCases) {

        return runAllTestCases(request, testCases, null);
    }

    private List<TestCaseExecutionResult> runAllTestCases(
            ExecutionRequest request,
            List<ProblemTestCase> testCases,
            Integer attemptRetryCount) {

        List<TestCaseExecutionResult> results = new ArrayList<>();

        for (ProblemTestCase testCase : testCases) {

            if (attemptRetryCount != null) {
                submissionService.refreshExecutionHeartbeat(
                        request.getSubmissionId(),
                        attemptRetryCount
                );
            }

            TestCaseExecutionResult result =
                    runTestCase(request, testCase);

            results.add(result);

            if (!result.isPassed()) {
                break;
            }
        }

        return results;
    }

    private ExecutionResult execute(
            ExecutionRequest request,
            List<ProblemTestCase> testCases,
            Integer attemptRetryCount) {

        if (testCases == null || testCases.isEmpty()) {
            throw new IllegalArgumentException(
                    "Problem has no test cases"
            );
        }

        List<TestCaseExecutionResult> results =
                runAllTestCases(request, testCases, attemptRetryCount);

        SubmissionVerdict verdict = determineVerdict(results);
        long totalExecutionTime = calculateTotalExecutionTime(results);
        long maxMemoryUsed = calculateMaxMemoryUsed(results);
        String errorMessage = findErrorMessage(results);

        return new ExecutionResult(
                verdict,
                totalExecutionTime,
                maxMemoryUsed,
                errorMessage,
                results
        );
    }

    private SubmissionVerdict determineVerdict(
            List<TestCaseExecutionResult> results) {

        if (results == null || results.isEmpty()) {
            return SubmissionVerdict.RUNTIME_ERROR;
        }

        for (TestCaseExecutionResult result : results) {

            SandboxErrorType errorType = result.getErrorType();

            if (errorType == SandboxErrorType.COMPILATION_ERROR) {
                return SubmissionVerdict.COMPILATION_ERROR;
            }

            if (errorType == SandboxErrorType.RUNTIME_ERROR) {
                return SubmissionVerdict.RUNTIME_ERROR;
            }

            if (errorType == SandboxErrorType.TIME_LIMIT_EXCEEDED) {
                return SubmissionVerdict.TIME_LIMIT_EXCEEDED;
            }

            if (errorType == SandboxErrorType.MEMORY_LIMIT_EXCEEDED) {
                return SubmissionVerdict.MEMORY_LIMIT_EXCEEDED;
            }

            if (!result.isPassed()) {
                return SubmissionVerdict.WRONG_ANSWER;
            }
        }

        return SubmissionVerdict.ACCEPTED;
    }

    private long calculateTotalExecutionTime(
            List<TestCaseExecutionResult> results) {

        long totalTime = 0L;

        for (TestCaseExecutionResult result : results) {
            if (result.getExecutionTime() != null) {
                totalTime += result.getExecutionTime();
            }
        }

        return totalTime;
    }

    private long calculateMaxMemoryUsed(
            List<TestCaseExecutionResult> results) {

        long maxMemory = 0L;

        for (TestCaseExecutionResult result : results) {
            if (result.getMemoryUsed() != null) {
                maxMemory = Math.max(
                        maxMemory,
                        result.getMemoryUsed()
                );
            }
        }

        return maxMemory;
    }

    private String findErrorMessage(
            List<TestCaseExecutionResult> results) {

        for (TestCaseExecutionResult result : results) {

            if (result.getErrorType() != SandboxErrorType.NONE) {
                return result.getErrorMessage();
            }
        }

        return null;
    }
}
