package com.codearena.codearena.controller;

import com.codearena.codearena.dto.ContestRegistrationResponse;
import com.codearena.codearena.dto.ContestRequest;
import com.codearena.codearena.entity.Contest;
import com.codearena.codearena.entity.ContestRegistration;
import com.codearena.codearena.service.ContestService;
import com.codearena.codearena.service.ContestRegistrationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

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
    public ResponseEntity<List<Contest>> getAllContests() {
        return ResponseEntity.ok(
                contestService.getAllContests()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contest> getContestById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                contestService.getContestById(id)
        );
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Contest> createContest(
            @Valid @RequestBody ContestRequest request) {

        Contest createdContest =
                contestService.createContest(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdContest);
    }

@PostMapping("/{id}/publish")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<Contest> publishContest(
        @PathVariable Long id) {

    Contest publishedContest = contestService.publishContest(id);

    return ResponseEntity.ok(publishedContest);
}
@GetMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<List<Contest>> getAllContestsForAdmin() {
    return ResponseEntity.ok(
            contestService.getAllContestsForAdmin()
    );
}

@PutMapping("/{id}")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<Contest> updateContest(
        @PathVariable Long id,
        @Valid @RequestBody ContestRequest request) {

    Contest updatedContest = contestService.updateContest(id, request);

    return ResponseEntity.ok(updatedContest);
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