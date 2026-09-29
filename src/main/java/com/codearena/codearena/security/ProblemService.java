package com.codearena.codearena.service;

import com.codearena.codearena.dto.ProblemRequest;
import com.codearena.codearena.dto.ProblemResponse;
import com.codearena.codearena.entity.Problem;
import com.codearena.codearena.repository.ProblemRepository;

import org.springframework.stereotype.Service;

@Service
public class ProblemService {

    private final ProblemRepository problemRepository;

    public ProblemService(
            ProblemRepository problemRepository) {
        this.problemRepository = problemRepository;
    }

    public ProblemResponse createProblem(
            ProblemRequest request) {

        Problem problem = new Problem();

        problem.setTitle(request.getTitle());
        problem.setDescription(request.getDescription());
        problem.setDifficulty(request.getDifficulty());
        problem.setConstraints(request.getConstraints());
        problem.setInputFormat(request.getInputFormat());
        problem.setOutputFormat(request.getOutputFormat());

        Problem savedProblem =
                problemRepository.save(problem);

        return new ProblemResponse(
                savedProblem.getId(),
                savedProblem.getTitle(),
                savedProblem.getDescription(),
                savedProblem.getDifficulty(),
                savedProblem.getConstraints(),
                savedProblem.getInputFormat(),
                savedProblem.getOutputFormat()
        );
    }
}