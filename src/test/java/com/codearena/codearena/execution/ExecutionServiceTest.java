package com.codearena.codearena.execution;

import com.codearena.codearena.entity.Submission;
import com.codearena.codearena.exception.SubmissionNotFoundException;
import com.codearena.codearena.repository.ProblemTestCaseRepository;
import com.codearena.codearena.entity.Problem;
import com.codearena.codearena.entity.SubmissionStatus;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import com.codearena.codearena.repository.SubmissionRepository;
import com.codearena.codearena.service.SubmissionService;
import java.util.Optional;
import com.codearena.codearena.entity.ProblemTestCase;
import com.codearena.codearena.entity.SubmissionLanguage;
import com.codearena.codearena.model.SandboxErrorType;
import com.codearena.codearena.model.TestCaseExecutionResult;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import com.codearena.codearena.entity.SubmissionVerdict;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class ExecutionServiceTest {

    @Mock
    private ProblemTestCaseRepository problemTestCaseRepository;

    @Mock
    private SubmissionRepository submissionRepository;

    @Mock
    private TestCaseRunner testCaseRunner;

    @Mock
    private SubmissionService submissionService;

    @InjectMocks
    private ExecutionService executionService;

    @Test
    void executeSubmissionThrowsWhenSubmissionDoesNotExist() {

        Long submissionId = 999L;

        when(submissionRepository.findById(submissionId))
                .thenReturn(Optional.empty());

        assertThrows(
                SubmissionNotFoundException.class,
                () -> executionService.executeSubmission(submissionId)
        );

        verify(submissionService).markAsRunning(submissionId);
    }

@Test
void executeSubmissionDoesNotContinueWhenMarkAsRunningFails() {

    Long submissionId = 10L;
    Submission submission = new Submission();
    submission.setStatus(SubmissionStatus.RUNNING);

    when(submissionRepository.findById(submissionId))
            .thenReturn(Optional.of(submission));

    org.mockito.Mockito.doThrow(
            new IllegalArgumentException(
                    "Only queued submissions can be marked as running"
            )
    ).when(submissionService).markAsRunning(submissionId);

    SubmissionClaimException thrown = assertThrows(
            SubmissionClaimException.class,
            () -> executionService.executeSubmission(submissionId)
    );

    verify(submissionService).markAsRunning(submissionId);
    verifyNoInteractions(testCaseRunner);
    org.junit.jupiter.api.Assertions.assertInstanceOf(
            IllegalArgumentException.class,
            thrown.getCause()
    );
}

@Test
void executeSubmissionHandlesTestCaseFetchFailure() {
    Long submissionId = 10L;

    Problem problem = new Problem();
    problem.setId(1L);

    Submission submission = new Submission();
    submission.setStatus(SubmissionStatus.QUEUED);
    submission.setProblem(problem);

    when(submissionRepository.findById(submissionId))
            .thenReturn(Optional.of(submission));

    when(problemTestCaseRepository.findByProblemId(1L))
            .thenThrow(new RuntimeException("Database error"));

    org.mockito.Mockito.doNothing()
            .when(submissionService).markAsRunning(submissionId);

    assertThrows(RuntimeException.class,
            () -> executionService.executeSubmission(submissionId));

    verify(submissionService).markAsRunning(submissionId);

    verify(submissionService, never()).updateExecutionResult(
            any(),
            any(),
            any(),
            any(),
            any()
    );
    verifyNoInteractions(testCaseRunner);
}

@Test
void executeSubmissionDoesNotRetryAsRuntimeErrorWhenSuccessUpdateFails() {
    Long submissionId = 10L;

    Problem problem = new Problem();
    problem.setId(1L);

    Submission submission = new Submission();
    submission.setStatus(SubmissionStatus.QUEUED);
    submission.setProblem(problem);
    submission.setLanguage(SubmissionLanguage.JAVA);
    submission.setSourceCode("class Main {}");

    ProblemTestCase testCase = new ProblemTestCase();
    testCase.setId(1L);
    testCase.setInput("1");
    testCase.setExpectedOutput("1");

    when(submissionRepository.findById(submissionId))
            .thenReturn(Optional.of(submission));

    when(problemTestCaseRepository.findByProblemId(1L))
            .thenReturn(List.of(testCase));

    org.mockito.Mockito.doNothing()
            .when(submissionService).markAsRunning(submissionId);

    TestCaseExecutionResult passedResult = new TestCaseExecutionResult(
            1L,
            true,
            10L,
            20L,
            "1",
            null,
            SandboxErrorType.NONE
    );

    when(testCaseRunner.run(any(), any()))
            .thenReturn(passedResult);

    doThrow(new RuntimeException("Database update failed"))
            .when(submissionService).updateExecutionResult(
                    submissionId,
                    SubmissionStatus.COMPLETED,
                    SubmissionVerdict.ACCEPTED,
                    10L,
                    20L
            );

    assertThrows(
            RuntimeException.class,
            () -> executionService.executeSubmission(submissionId)
    );

    verify(submissionService).markAsRunning(submissionId);

    verify(submissionService).updateExecutionResult(
            submissionId,
            SubmissionStatus.COMPLETED,
            SubmissionVerdict.ACCEPTED,
            10L,
            20L
    );

    verify(submissionService, never()).updateExecutionResult(
            submissionId,
            SubmissionStatus.COMPLETED,
            SubmissionVerdict.RUNTIME_ERROR,
            0L,
            0L
    );
}

@Test
void executeSubmissionPreservesOriginalExceptionWhenErrorUpdateFails() {
    Long submissionId = 10L;

    Problem problem = new Problem();
    problem.setId(1L);

    Submission submission = new Submission();
    submission.setStatus(SubmissionStatus.QUEUED);
    submission.setProblem(problem);

    when(submissionRepository.findById(submissionId))
            .thenReturn(Optional.of(submission));

    RuntimeException executionException =
            new RuntimeException("Test case database error");

    RuntimeException updateException =
            new RuntimeException("Failed to save runtime error");

    when(problemTestCaseRepository.findByProblemId(1L))
            .thenThrow(executionException);

    org.mockito.Mockito.doNothing()
            .when(submissionService).markAsRunning(submissionId);

    doThrow(updateException)
            .when(submissionService).updateExecutionResult(
                    submissionId,
                    SubmissionStatus.COMPLETED,
                    SubmissionVerdict.RUNTIME_ERROR,
                    0L,
                    0L
            );

    RuntimeException thrown = assertThrows(
            RuntimeException.class,
            () -> executionService.executeSubmission(submissionId)
    );

    org.junit.jupiter.api.Assertions.assertSame(executionException, thrown);
    verify(submissionService, never()).updateExecutionResult(
            any(),
            any(),
            any(),
            any(),
            any()
    );
}

}
