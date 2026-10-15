package com.codearena.codearena.repository;

import com.codearena.codearena.entity.Contest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContestRepository
        extends JpaRepository<Contest, Long> {
}
