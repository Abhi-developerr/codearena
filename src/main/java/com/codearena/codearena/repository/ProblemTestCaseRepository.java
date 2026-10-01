package com.codearena.codearena.repository;

import com.codearena.codearena.entity.ProblemTestCase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProblemTestCaseRepository
        extends JpaRepository<ProblemTestCase, Long> {
}