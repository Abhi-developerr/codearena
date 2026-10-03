package com.codearena.codearena.repository;

import com.codearena.codearena.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SubmissionRepository
        extends JpaRepository<Submission, Long> {

                List<Submission> findByUserIdOrderByCreatedAtDesc(
        Long userId
);

Optional<Submission> findByIdAndUserId(
        Long id,
        Long userId
);

List<Submission> findByUserIdAndProblemIdOrderByCreatedAtDesc(
        Long userId,
        Long problemId
);

}