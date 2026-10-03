package com.codearena.codearena.service;

import com.codearena.codearena.dto.SubmissionRequest;
import com.codearena.codearena.dto.SubmissionResponse;
import java.util.List;
import java.util.stream.Collectors;
import com.codearena.codearena.entity.Problem;
import com.codearena.codearena.entity.Submission;
import com.codearena.codearena.entity.SubmissionStatus;
import com.codearena.codearena.entity.User;
import com.codearena.codearena.exception.ProblemNotFoundException;
import com.codearena.codearena.exception.UserNotFoundException;
import com.codearena.codearena.repository.ProblemRepository;
import com.codearena.codearena.repository.SubmissionRepository;
import com.codearena.codearena.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            request.getSourceCode().trim()
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
public List<SubmissionResponse> getMySubmissions(
        Long userId) {

    userRepository.findById(userId)
            .orElseThrow(() ->
                    new UserNotFoundException(
                            "User not found"
                    )
            );

    return submissionRepository
            .findByUserIdOrderByCreatedAtDesc(userId)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
}

}