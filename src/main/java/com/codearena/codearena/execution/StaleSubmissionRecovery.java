package com.codearena.codearena.execution;

import com.codearena.codearena.entity.Submission;
import com.codearena.codearena.entity.SubmissionStatus;
import com.codearena.codearena.repository.SubmissionRepository;
import com.codearena.codearena.service.SubmissionService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class StaleSubmissionRecovery {

    private static final Logger logger =
            LoggerFactory.getLogger(StaleSubmissionRecovery.class);

    private final SubmissionRepository submissionRepository;
    private final SubmissionService submissionService;

    @Value("${submission-queue.stale-after-minutes:5}")
    private long staleAfterMinutes;

    @Value("${submission-queue.max-retries:3}")
    private int maxRetries;

    public StaleSubmissionRecovery(
            SubmissionRepository submissionRepository,
            SubmissionService submissionService) {

        this.submissionRepository = submissionRepository;
        this.submissionService = submissionService;
    }

    @Scheduled(
            fixedDelayString = "${submission-queue.recovery-interval:60000}"
    )
    public void recoverStaleSubmissions() {

        Instant cutoffTime = Instant.now()
                .minus(staleAfterMinutes, ChronoUnit.MINUTES);

        List<Submission> staleSubmissions =
                submissionRepository.findByStatusAndStartedAtBefore(
                        SubmissionStatus.RUNNING,
                        cutoffTime
                );

        for (Submission submission : staleSubmissions) {
            try {
                submissionService.handleExecutionFailure(
                        submission.getId(),
                        maxRetries
                );

                logger.warn(
                        "Recovered stale submission {}",
                        submission.getId()
                );

            } catch (RuntimeException exception) {
                logger.error(
                        "Could not recover stale submission {}",
                        submission.getId(),
                        exception
                );
            }
        }
    }
}