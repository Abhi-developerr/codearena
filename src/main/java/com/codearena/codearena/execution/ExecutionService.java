package com.codearena.codearena.execution;

import com.codearena.codearena.entity.ProblemTestCase;
import com.codearena.codearena.repository.ProblemTestCaseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExecutionService {

    private final ProblemTestCaseRepository problemTestCaseRepository;

    public ExecutionService(
            ProblemTestCaseRepository problemTestCaseRepository) {

        this.problemTestCaseRepository = problemTestCaseRepository;
    }

    public List<ProblemTestCase> getTestCases(Long problemId) {

        return problemTestCaseRepository.findByProblemId(problemId);
    }
}