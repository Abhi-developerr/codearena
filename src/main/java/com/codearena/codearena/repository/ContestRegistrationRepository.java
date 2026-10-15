package com.codearena.codearena.repository;

import com.codearena.codearena.entity.ContestRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContestRegistrationRepository
        extends JpaRepository<ContestRegistration, Long> {

    boolean existsByContestIdAndUserId(Long contestId, Long userId);
}
