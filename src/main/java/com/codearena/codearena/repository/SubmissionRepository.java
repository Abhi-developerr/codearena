package com.codearena.codearena.repository;

import com.codearena.codearena.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SubmissionRepository
        extends JpaRepository<Submission, Long> {

                List<Submission> findByUserIdOrderByCreatedAtDesc(
        Long userId
);

Optional<Submission> findByIdAndUserId(
        Long id,
        Long userId
);

Page<Submission> findByUserId(
        Long userId,
        Pageable pageable
);

}