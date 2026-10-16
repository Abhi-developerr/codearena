package com.codearena.codearena.execution;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codearena.codearena.entity.Submission;
import com.codearena.codearena.entity.SubmissionStatus;
import com.codearena.codearena.repository.SubmissionRepository;
import com.codearena.codearena.service.SubmissionService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class SubmissionQueueProcessorTest {

    @Mock
    private SubmissionRepository submissionRepository;

    @Mock
    private ExecutionService executionService;

    @Mock
    private SubmissionService submissionService;

    @InjectMocks
    private SubmissionQueueProcessor submissionQueueProcessor;

    @Test
    void processQueuedSubmissionsExecutesEveryQueuedSubmission() {
        Submission firstSubmission = submissionWithId(1L);
        Submission secondSubmission = submissionWithId(2L);

        when(submissionRepository.findByStatusOrderByCreatedAtAsc(
                SubmissionStatus.QUEUED,
                PageRequest.of(0, 50)
        ))
                .thenReturn(List.of(firstSubmission, secondSubmission));

        submissionQueueProcessor.processQueuedSubmissions();

        verify(executionService).executeSubmission(1L);
        verify(executionService).executeSubmission(2L);
    }

    @Test
    void processQueuedSubmissionsContinuesAfterOneExecutionFails() {
        Submission firstSubmission = submissionWithId(1L);
        Submission secondSubmission = submissionWithId(2L);

        when(submissionRepository.findByStatusOrderByCreatedAtAsc(
                SubmissionStatus.QUEUED,
                PageRequest.of(0, 50)
        ))
                .thenReturn(List.of(firstSubmission, secondSubmission));
        doThrow(new IllegalArgumentException("Already claimed"))
                .when(executionService).executeSubmission(1L);

        submissionQueueProcessor.processQueuedSubmissions();

        verify(executionService).executeSubmission(1L);
        verify(executionService).executeSubmission(2L);
    }

    private Submission submissionWithId(Long submissionId) {
        Submission submission = org.mockito.Mockito.mock(Submission.class);
        when(submission.getId()).thenReturn(submissionId);
        return submission;
    }
}
