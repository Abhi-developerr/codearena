
package com.codearena.codearena.service;

import com.codearena.codearena.entity.Contest;
import com.codearena.codearena.entity.ContestRegistration;
import com.codearena.codearena.repository.ContestRegistrationRepository;
import com.codearena.codearena.repository.ContestRepository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ContestRegistrationService {

    private final ContestRegistrationRepository registrationRepository;
    private final ContestRepository contestRepository;

    public ContestRegistrationService(
            ContestRegistrationRepository registrationRepository,
            ContestRepository contestRepository) {
        this.registrationRepository = registrationRepository;
        this.contestRepository = contestRepository;
    }

    @Transactional
    public ContestRegistration registerUser(
            Long contestId, Long userId) {

        Contest contest = contestRepository.findById(contestId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Contest not found"
                        )
                );
        if (!contest.isPublished() || contest.isArchived()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Contest not found"
            );
        }

        if (!LocalDateTime.now().isBefore(contest.getStartTime())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Registration is closed because the contest has started"
            );
        }

        if (registrationRepository
                .existsByContestIdAndUserId(contestId, userId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "User is already registered for this contest"
            );
        }

        ContestRegistration registration =
                new ContestRegistration();

        registration.setContest(contest);
        registration.setUserId(userId);

        try {
            return registrationRepository.saveAndFlush(registration);
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "User is already registered for this contest",
                    exception
            );
        }
    }

@Transactional(readOnly = true)
public long getRegistrationCount(Long contestId) {
    if (!contestRepository.existsById(contestId)) {
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Contest not found"
        );
    }

    return registrationRepository.countByContestId(contestId);
}

@Transactional
public void cancelRegistration(Long contestId, Long userId) {

    Contest contest = contestRepository.findById(contestId)
            .orElseThrow(() ->
                    new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Contest not found"
                    )
            );

if (contest.isArchived()) {
    throw new ResponseStatusException(
            HttpStatus.CONFLICT,
            "Cannot cancel registration for an archived contest"
    );
}

    if (!LocalDateTime.now().isBefore(contest.getStartTime())) {
        throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Registration cannot be cancelled after contest starts"
        );
    }

    ContestRegistration registration =
            registrationRepository
                    .findByContestIdAndUserId(contestId, userId)
                    .orElseThrow(() ->
                            new ResponseStatusException(
                                    HttpStatus.NOT_FOUND,
                                    "Registration not found"
                            )
                    );

    registrationRepository.delete(registration);
}

@Transactional(readOnly = true)
public java.util.List<ContestRegistration> getRegistrationsByContest(
        Long contestId) {

    if (!contestRepository.existsById(contestId)) {
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Contest not found"
        );
    }

    return registrationRepository.findByContestId(contestId);
}

@Transactional(readOnly = true)
public java.util.List<ContestRegistration> getMyRegistrations(
        Long userId) {
    return registrationRepository.findByUserId(userId);
}



}
