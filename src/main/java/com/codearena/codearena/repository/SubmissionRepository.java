package com.codearena.codearena.repository;

import com.codearena.codearena.entity.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmissionRepository
        extends JpaRepository<Submission, Long> {
}