package com.codearena.codearena.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "submissions")
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubmissionLanguage language;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String sourceCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubmissionStatus status;

    @Enumerated(EnumType.STRING)
    private SubmissionVerdict verdict;

    private Long executionTime;

    private Long memoryUsed;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    public Submission() {
    }

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();

        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public Problem getProblem() {
        return problem;
    }
    public void setProblem(Problem problem) {
        this.problem = problem;
    }
    public SubmissionLanguage getLanguage() {
        return language;
    }
    public void setLanguage(SubmissionLanguage language) {
        this.language = language;
    }
    public String getSourceCode() {
        return sourceCode;
    }
    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }
    public SubmissionStatus getStatus() {
        return status;
    }
    public void setStatus(SubmissionStatus status) {
        this.status = status;
    }
    public SubmissionVerdict getVerdict() {
        return verdict;
    }
    public void setVerdict(SubmissionVerdict verdict) {
        this.verdict = verdict;
    }
    public Long getExecutionTime() {
        return executionTime;
    }
    public void setExecutionTime(Long executionTime) {
        this.executionTime = executionTime;
    }
    public Long getMemoryUsed() {
        return memoryUsed;
    }
    public void setMemoryUsed(Long memoryUsed) {
        this.memoryUsed = memoryUsed;
    }
    public Instant getCreatedAt() {
        return createdAt;
    }
    public Instant getUpdatedAt() {
        return updatedAt;
    }
    
}