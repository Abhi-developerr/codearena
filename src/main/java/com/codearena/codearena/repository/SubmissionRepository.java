package com.codearena.codearena.repository;

import com.codearena.codearena.entity.Submission;
import com.codearena.codearena.entity.SubmissionStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.time.Instant;
import org.springframework.data.repository.query.Param;


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

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            update Submission submission
            set submission.status = :runningStatus,
                submission.updatedAt = :updatedAt
            where submission.id = :submissionId
              and submission.status = :queuedStatus
            """)
    int claimQueuedSubmission(
            @Param("submissionId") Long submissionId,
            @Param("queuedStatus") SubmissionStatus queuedStatus,
            @Param("runningStatus") SubmissionStatus runningStatus,
            @Param("updatedAt") Instant updatedAt
    );

    List<Submission> findByStatusOrderByCreatedAtAsc(
        SubmissionStatus status,
        Pageable pageable
);

}
