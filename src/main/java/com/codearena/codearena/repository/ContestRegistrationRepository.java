package com.codearena.codearena.repository;

import com.codearena.codearena.entity.ContestRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ContestRegistrationRepository
        extends JpaRepository<ContestRegistration, Long> {

    boolean existsByContestIdAndUserId(Long contestId, Long userId);

    List<ContestRegistration> findByUserId(Long userId);
    List<ContestRegistration> findByContestId(Long contestId);
}
