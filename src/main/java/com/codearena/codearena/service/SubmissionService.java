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
        SubmissionStatus status,
        SubmissionVerdict verdict,
        Long executionTime,
        Long memoryUsed) {

    if (status != SubmissionStatus.COMPLETED) {
        throw new IllegalArgumentException(
                "Execution result must have COMPLETED status"
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
   Submission submission =
        submissionRepository.findById(submissionId)
                .orElseThrow(() ->
                        new SubmissionNotFoundException(
                                "Submission not found"
                        )
                );

if (submission.getStatus()
        != SubmissionStatus.RUNNING) {

    throw new IllegalArgumentException(
            "Only running submissions can be completed"
    );
}

submission.setStatus(
        SubmissionStatus.COMPLETED
);

submission.setVerdict(verdict);
submission.setExecutionTime(executionTime);
submission.setMemoryUsed(memoryUsed);

submissionRepository.save(submission);
}

@Transactional
public void markAsRunning(Long submissionId) {

    Submission submission =
            submissionRepository.findById(submissionId)
                    .orElseThrow(() ->
                            new SubmissionNotFoundException(
                                    "Submission not found"
                            )
                    );

    if (submission.getStatus()
            != SubmissionStatus.QUEUED) {

        throw new IllegalArgumentException(
                "Only queued submissions can be marked as running"
        );
    }

    submission.setStatus(
            SubmissionStatus.RUNNING
    );

    submissionRepository.save(submission);
}

public Page<SubmissionResponse> getAllSubmissions(
        Pageable pageable) {

    return submissionRepository
            .findAll(pageable)
            .map(this::toResponse);
}

}