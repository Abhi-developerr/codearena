package com.codearena.codearena.service;

import com.codearena.codearena.entity.Contest;
import com.codearena.codearena.repository.ContestRegistrationRepository;
import com.codearena.codearena.repository.ContestRepository;

import jakarta.validation.Valid;

import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import com.codearena.codearena.dto.ContestRequest;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ContestService {

    private final ContestRepository contestRepository;
    private final ContestRegistrationRepository registrationRepository;

    public ContestService(ContestRepository contestRepository, ContestRegistrationRepository registrationRepository) {
        this.contestRepository = contestRepository;
        this.registrationRepository = registrationRepository;
    }

    public List<Contest> getAllContests() {
        return contestRepository.findByPublishedTrueAndArchivedFalse();
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

if (!contest.isPublished() || contest.isArchived()) {
    throw new ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "Contest not found with id: " + id
    );
}
    return contest;
}

  
@Transactional
public Contest createContest(ContestRequest request) {
    if (!request.getEndTime().isAfter(request.getStartTime())) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Contest end time must be after start time"
        );
    }

    Contest contest = new Contest();
    contest.setTitle(request.getTitle());
    contest.setDescription(request.getDescription());
    contest.setStartTime(request.getStartTime());
    contest.setEndTime(request.getEndTime());
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

if (!LocalDateTime.now().isBefore(contest.getStartTime())) {
    throw new ResponseStatusException(
            HttpStatus.CONFLICT,
            "Cannot publish a contest after its start time"
    );
}

    contest.setPublished(true);

    return contestRepository.save(contest);
    }
   
@Transactional
public Contest updateContest(Long id, ContestRequest request) {
    Contest contest = contestRepository.findById(id)
            .orElseThrow(() ->
                    new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Contest not found with id: " + id
                    )
            );

    if (!request.getEndTime().isAfter(request.getStartTime())) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Contest end time must be after start time"
        );
    }

    contest.setTitle(request.getTitle());
    contest.setDescription(request.getDescription());
    contest.setStartTime(request.getStartTime());
    contest.setEndTime(request.getEndTime());

    return contestRepository.save(contest);
}

@Transactional
public void deleteContest(Long id) {
    Contest contest = contestRepository.findById(id)
            .orElseThrow(() ->
                    new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Contest not found"
                    )
            );

    if (registrationRepository.existsByContestId(id)) {
        throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Cannot delete a contest that has registrations"
        );
    }

    contestRepository.delete(contest);
}

@Transactional
public Contest archiveContest(Long id) {
    Contest contest = contestRepository.findById(id)
            .orElseThrow(() ->
                    new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Contest not found"
                    )
            );

    contest.setArchived(true);
    contest.setPublished(false);

    return contestRepository.save(contest);
}


}