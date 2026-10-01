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
import com.codearena.codearena.exception.ProblemTitleAlreadyExistsException;
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
String title = request.getTitle().trim();

if (problemRepository.existsByTitle(title)) {
    throw new ProblemTitleAlreadyExistsException(
            "Problem title already exists"
    );
}
problem.setTitle(title);
        Problem savedProblem = problemRepository.save(problem);
        return toResponse(savedProblem);
    }

    public ProblemResponse getProblemById(Long id) {
        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new ProblemNotFoundException("Problem not found"));

        return toResponse(problem);
    }

    public Page<ProblemResponse> getAllProblems(Difficulty difficulty, String search, Pageable pageable) {
        String normalizedSearch = search == null ? "" : search.trim();

        if (difficulty != null && !normalizedSearch.isEmpty()) {
            return problemRepository.findByTitleContainingIgnoreCaseAndDifficulty(normalizedSearch, difficulty, pageable)
                    .map(this::toResponse);
        }

        if (difficulty != null) {
            return problemRepository.findByDifficulty(difficulty, pageable)
                    .map(this::toResponse);
        }

        if (!normalizedSearch.isEmpty()) {
            return problemRepository.findByTitleContainingIgnoreCase(normalizedSearch, pageable)
                    .map(this::toResponse);
        }

        return problemRepository.findAll(pageable)
                .map(this::toResponse);
    }

    @Transactional
    public ProblemResponse updateProblem(Long id, ProblemRequest request) {
        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new ProblemNotFoundException("Problem not found"));

        String title = request.getTitle().trim();

        if (problemRepository.existsByTitleAndIdNot(title, id)) {
            throw new ProblemTitleAlreadyExistsException(
                    "Problem title already exists"
            );
        }
        problem.setTitle(title);
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