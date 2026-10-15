
package com.codearena.codearena.execution;

import com.codearena.codearena.entity.Submission;
import com.codearena.codearena.exception.SubmissionNotFoundException;
import com.codearena.codearena.repository.ProblemTestCaseRepository;
import com.codearena.codearena.entity.SubmissionStatus;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import com.codearena.codearena.repository.SubmissionRepository;
import com.codearena.codearena.service.SubmissionService;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

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

        verify(submissionService, never())
                .markAsRunning(submissionId);
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

    assertThrows(
            IllegalArgumentException.class,
            () -> executionService.executeSubmission(submissionId)
    );

    verify(submissionService).markAsRunning(submissionId);
    verifyNoInteractions(testCaseRunner);
}

@Test
void executeSubmissionHandlesTestCaseFetchFailure() {

    Long submissionId = 10L;

    Submission submission = new Submission();
    submission.setStatus(SubmissionStatus.QUEUED);

    when(submissionRepository.findById(submissionId))
            .thenReturn(Optional.of(submission));

    org.mockito.Mockito.doNothing()
            .when(submissionService)
            .markAsRunning(submissionId);

    org.mockito.Mockito.when(
            problemTestCaseRepository.findByProblemId(
                    org.mockito.ArgumentMatchers.anyLong()
            )
    ).thenThrow(new RuntimeException("Database error"));

    assertThrows(
            RuntimeException.class,
            () -> executionService.executeSubmission(submissionId)
    );

    verify(submissionService).markAsRunning(submissionId);
    verifyNoInteractions(testCaseRunner);
}

}
