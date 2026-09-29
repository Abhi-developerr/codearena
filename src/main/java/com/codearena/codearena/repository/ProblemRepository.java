package com.codearena.codearena.repository;

import com.codearena.codearena.entity.Difficulty;
import com.codearena.codearena.entity.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import com.codearena.codearena.entity.Difficulty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProblemRepository
        extends JpaRepository<Problem, Long> {

                Page<Problem> findByDifficulty(
        Difficulty difficulty,
        Pageable pageable
);

}