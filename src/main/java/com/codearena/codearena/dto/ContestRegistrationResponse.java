
package com.codearena.codearena.dto;

import java.time.LocalDateTime;

public class ContestRegistrationResponse {

    private Long id;
    private Long contestId;
    private Long userId;
    private LocalDateTime registeredAt;

    public ContestRegistrationResponse(
            Long id,
            Long contestId,
            Long userId,
            LocalDateTime registeredAt) {
        this.id = id;
        this.contestId = contestId;
        this.userId = userId;
        this.registeredAt = registeredAt;
    }

    public Long getId() {
        return id;
    }

    public Long getContestId() {
        return contestId;
    }

    public Long getUserId() {
        return userId;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }
}
