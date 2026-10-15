package com.codearena.codearena.service;

import com.codearena.codearena.entity.Contest;
import com.codearena.codearena.repository.ContestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ContestService {

    private final ContestRepository contestRepository;

    public ContestService(ContestRepository contestRepository) {
        this.contestRepository = contestRepository;
    }

    public List<Contest> getAllContests() {
        return contestRepository.findAll();
    }

    public Contest getContestById(Long id) {
        return contestRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Contest not found with id: " + id)
                );
    }

    @Transactional
    public Contest createContest(Contest contest) {
        if (contest.getStartTime() == null
                || contest.getEndTime() == null) {
            throw new IllegalArgumentException(
                    "Contest start time and end time are required"
            );
        }

        if (!contest.getEndTime().isAfter(contest.getStartTime())) {
            throw new IllegalArgumentException(
                    "Contest end time must be after start time"
            );
        }

        contest.setPublished(false);

        return contestRepository.save(contest);
    }
}