
package com.codearena.codearena.execution;

import com.codearena.codearena.entity.Submission;
import com.codearena.codearena.entity.SubmissionStatus;
import com.codearena.codearena.repository.SubmissionRepository;
import com.codearena.codearena.service.SubmissionService;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class SubmissionQueueProcessor {

    private static final Logger logger =
            LoggerFactory.getLogger(SubmissionQueueProcessor.class);

    private static final int BATCH_SIZE = 50;

    private final SubmissionRepository submissionRepository;
    private final ExecutionService executionService;
    private final SubmissionService submissionService;

    @Value("${submission-queue.max-retries:3}")
    private int maxRetries;

    public SubmissionQueueProcessor(
            SubmissionRepository submissionRepository,
            ExecutionService executionService,
            SubmissionService submissionService) {

        this.submissionRepository = submissionRepository;
        this.executionService = executionService;
        this.submissionService = submissionService;
    }

    @Scheduled(fixedDelayString = "${submission-queue.poll-interval:1000}")
    public void processQueuedSubmissions() {

        List<Submission> queuedSubmissions =
                submissionRepository.findByStatusOrderByCreatedAtAsc(
                        SubmissionStatus.QUEUED,
                        PageRequest.of(0, BATCH_SIZE)
                );

        for (Submission submission : queuedSubmissions) {
            processSubmission(submission.getId());
        }
    }

    private void processSubmission(Long submissionId) {

        try {
            executionService.executeSubmission(submissionId);

        } catch (RuntimeException exception) {

            logger.warn(
                    "Execution failed for submission {}. Attempting recovery.",
                    submissionId,
                    exception
            );

            try {
                submissionService.handleExecutionFailure(
                        submissionId,
                        maxRetries
                );

            } catch (RuntimeException recoveryException) {

                logger.warn(
                        "Could not recover submission {}. Queue processing will continue.",
                        submissionId,
                        recoveryException
                );
            }
        }
    }
}
