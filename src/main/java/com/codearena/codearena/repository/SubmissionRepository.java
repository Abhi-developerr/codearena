package com.codearena.codearena.repository;

import com.codearena.codearena.entity.Submission;
import com.codearena.codearena.entity.SubmissionStatus;
import com.codearena.codearena.entity.SubmissionVerdict;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
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

                @Modifying
@Query("""
    update Submission submission
    set submission.status = :completedStatus,
        submission.verdict = :verdict,
        submission.executionTime = :executionTime,
        submission.memoryUsed = :memoryUsed,
        submission.completedAt = :completedAt,
        submission.updatedAt = :completedAt,
        submission.nextAttemptAt = null
    where submission.id = :submissionId
      and submission.status = :runningStatus
      and submission.retryCount = :retryCount
    """)
int completeExecutionAttempt(
        @Param("submissionId") Long submissionId,
        @Param("runningStatus") SubmissionStatus runningStatus,
        @Param("completedStatus") SubmissionStatus completedStatus,
        @Param("retryCount") Integer retryCount,
        @Param("verdict") SubmissionVerdict verdict,
        @Param("executionTime") Long executionTime,
        @Param("memoryUsed") Long memoryUsed,
        @Param("completedAt") Instant completedAt
);
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select submission
            from Submission submission
            where submission.id = :submissionId
              and submission.status = :status
              and submission.updatedAt < :staleThreshold
            """)
    Optional<Submission> findStaleRunningSubmissionForUpdate(
            @Param("submissionId") Long submissionId,
            @Param("status") SubmissionStatus status,
            @Param("staleThreshold") Instant staleThreshold
    );
    @Query("""
        select submission
        from Submission submission
        where submission.status = :status
          and (
              submission.nextAttemptAt is null
              or submission.nextAttemptAt <= :now
          )
        order by submission.createdAt asc
        """)
List<Submission> findEligibleQueuedSubmissions(
        @Param("status") SubmissionStatus status,
        @Param("now") Instant now,
        Pageable pageable
);

                
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
            submission.startedAt = :startedAt,
            submission.completedAt = null,
            submission.updatedAt = :startedAt
        where submission.id = :submissionId
          and submission.status = :queuedStatus
        """)
int claimQueuedSubmission(
        @Param("submissionId") Long submissionId,
        @Param("queuedStatus") SubmissionStatus queuedStatus,
        @Param("runningStatus") SubmissionStatus runningStatus,
        @Param("startedAt") Instant startedAt
);
@Modifying(flushAutomatically = true, clearAutomatically = true)
@Query("""
        update Submission submission
        set submission.updatedAt = :heartbeatAt
        where submission.id = :submissionId
          and submission.status = :runningStatus
        """)
int refreshExecutionHeartbeat(
        @Param("submissionId") Long submissionId,
        @Param("runningStatus") SubmissionStatus runningStatus,
        @Param("heartbeatAt") Instant heartbeatAt
);
    List<Submission> findByStatusOrderByCreatedAtAsc(
        SubmissionStatus status,
        Pageable pageable
);

List<Submission> findByStatusAndStartedAtBefore(
        SubmissionStatus status,
        Instant startedAt
);

List<Submission> findByStatusAndUpdatedAtBefore(
        SubmissionStatus status,
        Instant updatedAt
);

}
