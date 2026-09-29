package com.codearena.codearena.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codearena.codearena.dto.ProblemRequest;
import com.codearena.codearena.dto.ProblemResponse;
import com.codearena.codearena.entity.Difficulty;
import com.codearena.codearena.entity.Problem;
import com.codearena.codearena.exception.ProblemNotFoundException;
import com.codearena.codearena.repository.ProblemRepository;

@Service
public class ProblemService {

    private final ProblemRepository problemRepository;

    public ProblemService(ProblemRepository problemRepository) {
        this.problemRepository = problemRepository;
    }

    public ProblemResponse createProblem(ProblemRequest request) {
        Problem problem = new Problem();
        problem.setTitle(request.getTitle());
        problem.setDescription(request.getDescription());
        problem.setDifficulty(request.getDifficulty());
        problem.setConstraints(request.getConstraints());
        problem.setInputFormat(request.getInputFormat());
        problem.setOutputFormat(request.getOutputFormat());

        Problem savedProblem = problemRepository.save(problem);
        return toResponse(savedProblem);
    }

    public ProblemResponse getProblemById(Long id) {
        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new ProblemNotFoundException("Problem not found"));

        return toResponse(problem);
    }

    public Page<ProblemResponse> getAllProblems(Difficulty difficulty, Pageable pageable) {
        return problemRepository.findAll(pageable)
                .map(this::toResponse);
    }

    @Transactional
    public ProblemResponse updateProblem(Long id, ProblemRequest request) {
        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new ProblemNotFoundException("Problem not found"));

        problem.setTitle(request.getTitle());
        problem.setDescription(request.getDescription());
        problem.setDifficulty(request.getDifficulty());
        problem.setConstraints(request.getConstraints());
        problem.setInputFormat(request.getInputFormat());
        problem.setOutputFormat(request.getOutputFormat());

        Problem updatedProblem = problemRepository.save(problem);
        return toResponse(updatedProblem);
    }

    @Transactional
    public void deleteProblem(Long id) {
        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new ProblemNotFoundException("Problem not found"));

        problemRepository.delete(problem);
    }

    private ProblemResponse toResponse(Problem problem) {
        return new ProblemResponse(
                problem.getId(),
                problem.getTitle(),
                problem.getDescription(),
                problem.getDifficulty(),
                problem.getConstraints(),
                problem.getInputFormat(),
                problem.getOutputFormat(),
                problem.getCreatedAt(),
                problem.getUpdatedAt()
        );
    }
}