
package com.codearena.codearena.controller;

import com.codearena.codearena.entity.ContestRegistration;
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
    public ResponseEntity<ContestRegistration> registerForContest(
            @PathVariable Long contestId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        ContestRegistration registration =
                registrationService.registerUser(
                        contestId,
                        userDetails.getId()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(registration);
    }


@GetMapping("/my-registrations")
public ResponseEntity<java.util.List<ContestRegistration>>
        getMyRegistrations(
        @AuthenticationPrincipal CustomUserDetails userDetails) {

    return ResponseEntity.ok(
            registrationService.getMyRegistrations(
                    userDetails.getId()
            )
    );
}



}
