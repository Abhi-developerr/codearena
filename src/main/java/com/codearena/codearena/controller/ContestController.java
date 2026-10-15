package com.codearena.codearena.controller;

import com.codearena.codearena.entity.Contest;
import com.codearena.codearena.service.ContestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contests")
public class ContestController {

    private final ContestService contestService;

    public ContestController(ContestService contestService) {
        this.contestService = contestService;
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
            @RequestBody Contest contest) {

        Contest createdContest =
                contestService.createContest(contest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdContest);
    }
}