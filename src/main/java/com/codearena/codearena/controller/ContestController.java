package com.codearena.codearena.controller;

import com.codearena.codearena.dto.ContestRegistrationResponse;
import com.codearena.codearena.dto.ContestRequest;
import com.codearena.codearena.entity.Contest;
import com.codearena.codearena.entity.ContestRegistration;
import com.codearena.codearena.service.ContestService;
import com.codearena.codearena.service.ContestRegistrationService;
import com.codearena.codearena.dto.ContestResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/contests")
public class ContestController {

    private final ContestService contestService;
    private final ContestRegistrationService contestRegistrationService;

    public ContestController(
            ContestService contestService,
            ContestRegistrationService contestRegistrationService) {
        this.contestService = contestService;
        this.contestRegistrationService = contestRegistrationService;
    }

  
@GetMapping
public ResponseEntity<java.util.List<ContestResponse>> getAllContests() {

    java.util.List<ContestResponse> response =
            contestService.getAllContests()
                    .stream()
                    .map(contest -> new ContestResponse(
                            contest.getId(),
                            contest.getTitle(),
                            contest.getDescription(),
                            contest.getStartTime(),
                            contest.getEndTime(),
                            contest.isPublished()
                    ))
                    .toList();

    return ResponseEntity.ok(response);
}

@GetMapping("/{id}")
public ResponseEntity<ContestResponse> getContestById(
        @PathVariable Long id) {

    Contest contest = contestService.getContestById(id);

    ContestResponse response = new ContestResponse(
            contest.getId(),
            contest.getTitle(),
            contest.getDescription(),
            contest.getStartTime(),
            contest.getEndTime(),
            contest.isPublished()
    );

    return ResponseEntity.ok(response);
}
      
@PostMapping
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<ContestResponse> createContest(
        @Valid @RequestBody ContestRequest request) {

    Contest contest = contestService.createContest(request);

    ContestResponse response = new ContestResponse(
            contest.getId(),
            contest.getTitle(),
            contest.getDescription(),
            contest.getStartTime(),
            contest.getEndTime(),
            contest.isPublished()
    );

    return ResponseEntity.status(HttpStatus.CREATED)
            .body(response);
}

@PostMapping("/{id}/publish")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<ContestResponse> publishContest(
        @PathVariable Long id) {

    Contest contest = contestService.publishContest(id);

    ContestResponse response = new ContestResponse(
            contest.getId(),
            contest.getTitle(),
            contest.getDescription(),
            contest.getStartTime(),
            contest.getEndTime(),
            contest.isPublished()
    );

    return ResponseEntity.ok(response);
}

@PostMapping("/{id}/archive")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<ContestResponse> archiveContest(
        @PathVariable Long id) {

    Contest contest = contestService.archiveContest(id);

    ContestResponse response = new ContestResponse(
            contest.getId(),
            contest.getTitle(),
            contest.getDescription(),
            contest.getStartTime(),
            contest.getEndTime(),
            contest.isPublished()
    );

    return ResponseEntity.ok(response);
}

@GetMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<java.util.List<ContestResponse>>
        getAllContestsForAdmin() {

    java.util.List<ContestResponse> response =
            contestService.getAllContestsForAdmin()
                    .stream()
                    .map(contest -> new ContestResponse(
                            contest.getId(),
                            contest.getTitle(),
                            contest.getDescription(),
                            contest.getStartTime(),
                            contest.getEndTime(),
                            contest.isPublished()
                    ))
                    .toList();

    return ResponseEntity.ok(response);
}

@PutMapping("/{id}")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<ContestResponse> updateContest(
        @PathVariable Long id,
        @Valid @RequestBody ContestRequest request) {

    Contest contest = contestService.updateContest(id, request);

    ContestResponse response = new ContestResponse(
            contest.getId(),
            contest.getTitle(),
            contest.getDescription(),
            contest.getStartTime(),
            contest.getEndTime(),
            contest.isPublished()
    );

    return ResponseEntity.ok(response);
}

@GetMapping("/{id}/registrations/count")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<Long> getRegistrationCount(
        @PathVariable Long id) {

    return ResponseEntity.ok(
            contestRegistrationService.getRegistrationCount(id)
    );
}

@DeleteMapping("/{id}")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<Void> deleteContest(
        @PathVariable Long id) {

    contestService.deleteContest(id);

    return ResponseEntity.noContent().build();
}

@GetMapping("/{id}/registrations")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<java.util.List<ContestRegistrationResponse>>
        getContestRegistrations(@PathVariable Long id) {

    java.util.List<ContestRegistration> registrations =
            contestRegistrationService.getRegistrationsByContest(id);

    java.util.List<ContestRegistrationResponse> response =
            registrations.stream()
                    .map(registration ->
                            new ContestRegistrationResponse(
                                    registration.getId(),
                                    registration.getContest().getId(),
                                    registration.getUserId(),
                                    registration.getRegisteredAt()
                            )
                    )
                    .toList();

    return ResponseEntity.ok(response);
}

}