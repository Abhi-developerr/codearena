package com.codearena.codearena.service;

import com.codearena.codearena.dto.SubmissionCodeResponse;
import com.codearena.codearena.dto.SubmissionRequest;
import com.codearena.codearena.dto.SubmissionResponse;
import com.codearena.codearena.entity.Problem;
import com.codearena.codearena.entity.Submission;
import com.codearena.codearena.entity.SubmissionStatus;
import com.codearena.codearena.entity.SubmissionVerdict;
import com.codearena.codearena.entity.User;
import com.codearena.codearena.exception.ProblemNotFoundException;
import com.codearena.codearena.exception.UserNotFoundException;
import com.codearena.codearena.repository.ProblemRepository;
import com.codearena.codearena.repository.SubmissionRepository;
import com.codearena.codearena.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import com.codearena.codearena.exception.SubmissionNotFoundException;

@Service
public class SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;
    private final ProblemRepository problemRepository;

    public SubmissionService(
            SubmissionRepository submissionRepository,
            UserRepository userRepository,
            ProblemRepository problemRepository) {

        this.submissionRepository = submissionRepository;
        this.userRepository = userRepository;
        this.problemRepository = problemRepository;
    }
private static final long BASE_RETRY_DELAY_SECONDS = 5L;
private static final long MAX_RETRY_DELAY_SECONDS = 300L;

private Instant calculateNextAttemptAt(int retryCount) {

    int exponent = Math.min(
            Math.max(retryCount, 0),
            6
    );

    long delaySeconds = Math.min(
            MAX_RETRY_DELAY_SECONDS,
            BASE_RETRY_DELAY_SECONDS * (1L << exponent)
    );

    return Instant.now().plusSeconds(delaySeconds);
}
    @Transactional
    public SubmissionResponse createSubmission(
        Long userId,
        SubmissionRequest request) {

    User user =
            userRepository.findById(userId)
                    .orElseThrow(() ->
                            new UserNotFoundException(
                                    "User not found"
                            )
                    );

    Problem problem =
            problemRepository.findById(request.getProblemId())
                    .orElseThrow(() ->
                            new ProblemNotFoundException(
                                    "Problem not found"
                            )
                    );

    Submission submission =
            new Submission();

    submission.setUser(user);
    submission.setProblem(problem);

    submission.setLanguage(
            request.getLanguage()
    );

    submission.setSourceCode(
            request.getSourceCode()
    );

    submission.setStatus(
            SubmissionStatus.QUEUED
    );

    Submission savedSubmission =
            submissionRepository.save(submission);

    return toResponse(savedSubmission);
}

@Transactional
public boolean recoverStaleSubmission(
        Long submissionId,
        Instant staleThreshold,
        int maxRetries) {

    if (maxRetries < 0) {
        throw new IllegalArgumentException(
                "Maximum retries cannot be negative"
        );
    }

    Submission submission =
            submissionRepository.findStaleRunningSubmissionForUpdate(
                    submissionId,
                    SubmissionStatus.RUNNING,
                    staleThreshold
            ).orElse(null);

    if (submission == null) {
        return false;
    }

    Integer storedRetryCount = submission.getRetryCount();
    int retryCount = storedRetryCount == null ? 0 : storedRetryCount;

    if (retryCount < maxRetries) {

      
       
        submission.setStartedAt(null);
        submission.setCompletedAt(null);
        submission.setVerdict(null);
        submission.setExecutionTime(null);
        submission.setMemoryUsed(null);
        int nextRetryCount = retryCount + 1;

        submission.setRetryCount(nextRetryCount);
        submission.setStatus(SubmissionStatus.QUEUED);

        submission.setNextAttemptAt(
        calculateNextAttemptAt(nextRetryCount)
         );

    } else {

        submission.setStatus(SubmissionStatus.COMPLETED);
        submission.setVerdict(SubmissionVerdict.RUNTIME_ERROR);
        submission.setExecutionTime(0L);
        submission.setMemoryUsed(0L);
        submission.setCompletedAt(Instant.now());
        submission.setNextAttemptAt(null);
    }

    submissionRepository.save(submission);
    return true;
}

private SubmissionResponse toResponse(
        Submission submission) {

    return new SubmissionResponse(
            submission.getId(),
            submission.getProblem().getId(),
            submission.getLanguage(),
            submission.getStatus(),
            submission.getVerdict(),
            submission.getExecutionTime(),
            submission.getMemoryUsed(),
            submission.getCreatedAt(),
            submission.getUpdatedAt()
    );
}

@Transactional(readOnly = true)
public SubmissionResponse getMySubmission(
        Long userId,
        Long submissionId) {

    Submission submission =
            submissionRepository
                    .findByIdAndUserId(
                            submissionId,
                            userId
                    )
                    .orElseThrow(() ->
                            new SubmissionNotFoundException(
                                    "Submission not found"
                            )
                    );

    return toResponse(submission);
}

@Transactional(readOnly = true)
public Page<SubmissionResponse> getMySubmissions(
        Long userId,
        Pageable pageable) {

    userRepository.findById(userId)
            .orElseThrow(() ->
                    new UserNotFoundException(
                            "User not found"
                    )
            );

    return submissionRepository
            .findByUserId(userId, pageable)
            .map(this::toResponse);
}

    @Transactional(readOnly = true)
    public SubmissionCodeResponse getSubmissionCode(
        Long userId,
        Long submissionId) {

    Submission submission =
            submissionRepository
                    .findByIdAndUserId(
                            submissionId,
                            userId
                    )
                    .orElseThrow(() ->
                            new SubmissionNotFoundException(
                                    "Submission not found"
                            )
                    );

    return new SubmissionCodeResponse(
            submission.getId(),
            submission.getSourceCode()
    );
}

@Transactional
public void handleExecutionFailure(
        Long submissionId,
        Integer attemptRetryCount,
        int maxRetries
) {
        if (maxRetries < 0) {
    throw new IllegalArgumentException(
        "Maximum retries cannot be negative"
    );
}
    if (attemptRetryCount == null || attemptRetryCount < 0) {
        throw new IllegalArgumentException(
            "Attempt retry count must be non-negative"
        );
    }

    Submission submission = submissionRepository
        .findByIdForUpdate(submissionId)
        .orElseThrow(() -> new IllegalArgumentException(
            "Submission not found: " + submissionId
        ));

    // Ignore failures reported by an outdated execution attempt.
    if (submission.getStatus() != SubmissionStatus.RUNNING
            || !attemptRetryCount.equals(submission.getRetryCount())) {
        return;
    }

    int retryCount = submission.getRetryCount() == null
        ? 0
        : submission.getRetryCount();

    if (retryCount < maxRetries) {
        retryCount++;

        submission.setRetryCount(retryCount);
        submission.setStatus(SubmissionStatus.QUEUED);
        submission.setNextAttemptAt(calculateNextAttemptAt(retryCount));

        submission.setStartedAt(null);
        submission.setCompletedAt(null);
        submission.setVerdict(null);
        submission.setExecutionTime(null);
        submission.setMemoryUsed(null);
    } else {
        submission.setStatus(SubmissionStatus.COMPLETED);
        submission.setVerdict(SubmissionVerdict.RUNTIME_ERROR);
        submission.setCompletedAt(Instant.now());
        submission.setNextAttemptAt(null);
    }

    submissionRepository.save(submission);
}

@Transactional(readOnly = true)
public Page<SubmissionResponse> getMySubmissionsForProblem(
        Long userId,
        Long problemId,
        Pageable pageable) {

    userRepository.findById(userId)
            .orElseThrow(() ->
                    new UserNotFoundException(
                            "User not found"
                    )
            );

    problemRepository.findById(problemId)
            .orElseThrow(() ->
                    new ProblemNotFoundException(
                            "Problem not found"
                    )
            );

    return submissionRepository
            .findByUserIdAndProblemId(
                    userId,
                    problemId,
                    pageable
            )
            .map(this::toResponse);
}

@Transactional
public void updateExecutionResult(
        Long submissionId,
        Integer attemptRetryCount,
        SubmissionStatus status,
        SubmissionVerdict verdict,
        Long executionTime,
        Long memoryUsed) {

    if (status != SubmissionStatus.COMPLETED) {
        throw new IllegalArgumentException(
                "Execution result must have COMPLETED status"
        );
    }

    if (attemptRetryCount == null || attemptRetryCount < 0) {
        throw new IllegalArgumentException(
                "Attempt retry count must be zero or greater"
        );
    }

    if (verdict == null) {
        throw new IllegalArgumentException(
                "Completed submission must have a verdict"
        );
    }

    if (executionTime == null || executionTime < 0) {
        throw new IllegalArgumentException(
                "Execution time must be zero or greater"
        );
    }

    if (memoryUsed == null || memoryUsed < 0) {
        throw new IllegalArgumentException(
                "Memory used must be zero or greater"
        );
    }

    int updatedRows =
            submissionRepository.completeExecutionAttempt(
                    submissionId,
                    SubmissionStatus.RUNNING,
                    SubmissionStatus.COMPLETED,
                    attemptRetryCount,
                    verdict,
                    executionTime,
                    memoryUsed,
                    Instant.now()
            );

    if (updatedRows != 1) {
        throw new IllegalStateException(
                "Execution result rejected because the submission "
                        + "is no longer owned by this attempt"
        );
    }
}

@Transactional
public void markAsRunning(
        Long submissionId,
        Integer expectedRetryCount
) {
    if (expectedRetryCount == null || expectedRetryCount < 0) {
        throw new IllegalArgumentException(
            "Expected retry count must be non-negative"
        );
    }

    int claimed = submissionRepository.claimQueuedSubmission(
        submissionId,
        SubmissionStatus.QUEUED,
        SubmissionStatus.RUNNING,
        expectedRetryCount,
        Instant.now()
    );

    if (claimed != 1) {
        throw new IllegalArgumentException(
            "Submission could not be claimed for this attempt"
        );
    }
}

public Page<SubmissionResponse> getAllSubmissions(
        Pageable pageable) {

    return submissionRepository
            .findAll(pageable)
            .map(this::toResponse);
}

@Transactional
public void markUnexpectedExecutionFailure(Long submissionId) {

    Submission submission =
            submissionRepository.findById(submissionId)
                    .orElseThrow(() ->
                            new SubmissionNotFoundException(
                                    "Submission not found"
                            )
                    );

    if (submission.getStatus() != SubmissionStatus.RUNNING) {
        return;
    }

    submission.setStatus(SubmissionStatus.COMPLETED);
    submission.setVerdict(SubmissionVerdict.RUNTIME_ERROR);
    submission.setExecutionTime(0L);
    submission.setMemoryUsed(0L);
    submission.setCompletedAt(Instant.now());
    submissionRepository.save(submission);
}

@Transactional
public void refreshExecutionHeartbeat(
        Long submissionId,
        Integer attemptRetryCount) {

    if (attemptRetryCount == null || attemptRetryCount < 0) {
        throw new IllegalArgumentException(
                "Attempt retry count must be zero or greater"
        );
    }

    int updatedRows =
            submissionRepository.refreshExecutionHeartbeatForAttempt(
                    submissionId,
                    SubmissionStatus.RUNNING,
                    attemptRetryCount,
                    Instant.now()
            );

    if (updatedRows != 1) {
        throw new IllegalStateException(
                "Heartbeat rejected because the submission is no "
                        + "longer owned by this execution attempt"
        );
    }
}

}