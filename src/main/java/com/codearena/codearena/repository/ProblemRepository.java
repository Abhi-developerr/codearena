package com.codearena.codearena.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.codearena.codearena.entity.Difficulty;
import com.codearena.codearena.entity.Problem;

public interface ProblemRepository extends JpaRepository<Problem, Long> {

    Page<Problem> findByDifficulty(Difficulty difficulty, Pageable pageable);

    Page<Problem> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    Page<Problem> findByTitleContainingIgnoreCaseAndDifficulty(
            String title,
            Difficulty difficulty,
            Pageable pageable
    );
}