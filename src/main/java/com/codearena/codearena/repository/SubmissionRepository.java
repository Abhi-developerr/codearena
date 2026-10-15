package com.codearena.codearena.repository;

import com.codearena.codearena.entity.Submission;
import com.codearena.codearena.entity.SubmissionStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;


public interface SubmissionRepository
        extends JpaRepository<Submission, Long> {
    
                
    Page<Submission> findByUserId(
            Long userId,
            Pageable pageable
    );

    Optional<Submission> findByIdAndUserId(
            Long id,
            Long userId
    );

    Page<Submission> findByUserIdAndProblemId(
            Long userId,
            Long problemId,
            Pageable pageable
    );


List<Submission> findByStatus(
        SubmissionStatus status
);


}