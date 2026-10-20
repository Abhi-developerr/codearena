package com.codearena.codearena.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codearena.codearena.entity.Submission;
import com.codearena.codearena.entity.SubmissionStatus;
import com.codearena.codearena.repository.ProblemRepository;
import com.codearena.codearena.repository.SubmissionRepository;
import com.codearena.codearena.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SubmissionServiceTest {

    @Mock
    private SubmissionRepository submissionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProblemRepository problemRepository;

    private SubmissionService submissionService;

    @BeforeEach
    void setUp() {
        submissionService = new SubmissionService(
                submissionRepository,
                userRepository,
                problemRepository
        );
    }

    @Test
    void markAsRunningAtomicallyClaimsAQueuedSubmission() {
        Long submissionId = 1L;
        Submission submission = new Submission();
        submission.setStatus(SubmissionStatus.QUEUED);
        submission.setRetryCount(0);

        when(submissionRepository.claimQueuedSubmission(
                any(), any(), any(), any(), any()))
                .thenReturn(1);

        assertDoesNotThrow(() -> submissionService.markAsRunning(submissionId, 0));

        verify(submissionRepository).claimQueuedSubmission(
                org.mockito.ArgumentMatchers.eq(submissionId),
                org.mockito.ArgumentMatchers.eq(SubmissionStatus.QUEUED),
                org.mockito.ArgumentMatchers.eq(SubmissionStatus.RUNNING),
                org.mockito.ArgumentMatchers.eq(0),
                any()
        );
        verify(submissionRepository, never()).save(submission);
    }

    @Test
    void markAsRunningRejectsAConcurrentClaim() {
        Long submissionId = 1L;
        Submission submission = new Submission();
        submission.setStatus(SubmissionStatus.QUEUED);
        submission.setRetryCount(0);

        when(submissionRepository.claimQueuedSubmission(
                any(), any(), any(), any(), any()))
                .thenReturn(0);

        assertThrows(
                IllegalArgumentException.class,
                () -> submissionService.markAsRunning(submissionId, 0)
        );
    }
}
