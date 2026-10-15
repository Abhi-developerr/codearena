package com.codearena.codearena.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "contests")
public class Contest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

   @NotBlank(message = "Contest title is required")
@Size(max = 150, message = "Title cannot exceed 150 characters")
@Column(nullable = false, length = 150)
private String title;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
@Column(length = 1000)
private String description;

    @NotNull(message = "Contest start time is required")
@Column(nullable = false)
private LocalDateTime startTime;

@NotNull(message = "Contest end time is required")
@Column(nullable = false)
private LocalDateTime endTime;

    @Column(nullable = false)
    private boolean published = false;

    public Contest() {
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }
}