
package com.codearena.codearena.controller;

import com.codearena.codearena.entity.ContestRegistration;
import com.codearena.codearena.dto.ContestRegistrationResponse;
import com.codearena.codearena.security.CustomUserDetails;
import com.codearena.codearena.service.ContestRegistrationService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contests")
public class ContestRegistrationController {

    private final ContestRegistrationService registrationService;

    public ContestRegistrationController(
            ContestRegistrationService registrationService) {
        this.registrationService = registrationService;
    }

   
@PostMapping("/{contestId}/register")
public ResponseEntity<ContestRegistrationResponse> registerForContest(
        @PathVariable Long contestId,
        @AuthenticationPrincipal CustomUserDetails userDetails) {

    ContestRegistration registration =
            registrationService.registerUser(
                    contestId,
                    userDetails.getId()
            );

    ContestRegistrationResponse response =
            new ContestRegistrationResponse(
                    registration.getId(),
                    registration.getContest().getId(),
                    registration.getUserId(),
                    registration.getRegisteredAt()
            );

    return ResponseEntity.status(HttpStatus.CREATED)
            .body(response);
}

@GetMapping("/my-registrations")
public ResponseEntity<java.util.List<ContestRegistrationResponse>>
        getMyRegistrations(
        @AuthenticationPrincipal CustomUserDetails userDetails) {

    java.util.List<ContestRegistration> registrations =
            registrationService.getMyRegistrations(
                    userDetails.getId()
            );

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

@DeleteMapping("/{contestId}/register")
public ResponseEntity<Void> cancelRegistration(
        @PathVariable Long contestId,
        @AuthenticationPrincipal CustomUserDetails userDetails) {

    registrationService.cancelRegistration(
            contestId,
            userDetails.getId()
    );

    return ResponseEntity.noContent().build();
}


}
