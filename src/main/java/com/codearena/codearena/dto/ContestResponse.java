
package com.codearena.codearena.dto;

import java.time.LocalDateTime;

public class ContestResponse {

    private Long id;
    private String title;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean published;

    public ContestResponse(
            Long id,
            String title,
            String description,
            LocalDateTime startTime,
            LocalDateTime endTime,
            boolean published) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.published = published;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public boolean isPublished() {
        return published;
    }
}
