package com.codearena.codearena.repository;

import com.codearena.codearena.entity.ProblemTestCase;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ProblemTestCaseRepository
        extends JpaRepository<ProblemTestCase, Long> {
    List<ProblemTestCase> findByProblemId(Long problemId);
    List<ProblemTestCase> findByProblemIdAndHiddenFalse(Long problemId);
}