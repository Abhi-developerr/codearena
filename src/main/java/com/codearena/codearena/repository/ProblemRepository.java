package com.codearena.codearena.repository;

import com.codearena.codearena.entity.Problem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProblemRepository
        extends JpaRepository<Problem, Long> {

}