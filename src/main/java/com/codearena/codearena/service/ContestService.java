package com.codearena.codearena.service;

import com.codearena.codearena.entity.Contest;
import com.codearena.codearena.repository.ContestRepository;

import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
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
        return contestRepository.findByPublishedTrue();
    }

    public List<Contest> getAllContestsForAdmin() {
        return contestRepository.findAll();
    }


public Contest getContestById(Long id) {
    Contest contest = contestRepository.findById(id)
            .orElseThrow(() ->
    new ResponseStatusException(
        HttpStatus.NOT_FOUND,
        "Contest not found with id: " + id
    )
);

    if (!contest.isPublished()) {
        throw new ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "Contest not found with id: " + id
        );
    }

    return contest;
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


    @Transactional
    public Contest publishContest(Long id) {
    Contest contest = contestRepository.findById(id)
            .orElseThrow(() ->
                    new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Contest not found with id: " + id
                    )
            );

    contest.setPublished(true);

    return contestRepository.save(contest);
    }

    @Transactional
    public Contest updateContest(Long id, Contest updatedContest) {
        Contest contest = contestRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Contest not found with id: " + id
                        )
                );

        if (updatedContest.getTitle() != null) {
            contest.setTitle(updatedContest.getTitle());
        }
        if (updatedContest.getDescription() != null) {
            contest.setDescription(updatedContest.getDescription());
        }
        if (updatedContest.getStartTime() != null) {
            contest.setStartTime(updatedContest.getStartTime());
        }
        if (updatedContest.getEndTime() != null) {
            contest.setEndTime(updatedContest.getEndTime());
        }
        if (contest.getStartTime() == null
                || contest.getEndTime() == null
                || !contest.getEndTime().isAfter(contest.getStartTime())) {
            throw new IllegalArgumentException(
                    "Contest end time must be after start time"
            );
        }

        return contestRepository.save(contest);
    }


@Transactional
public void deleteContest(Long id) {
    Contest contest = contestRepository.findById(id)
            .orElseThrow(() ->
                    new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Contest not found with id: " + id
                    )
            );

    contestRepository.delete(contest);
}


}