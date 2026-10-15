
package com.codearena.codearena.repository;

import com.codearena.codearena.entity.Difficulty;
import com.codearena.codearena.entity.Problem;
import com.codearena.codearena.entity.ProblemTestCase;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class ProblemTestCaseRepositoryTest {

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private ProblemTestCaseRepository problemTestCaseRepository;

    @Test
    void findByProblemIdAndHiddenFalseReturnsOnlyVisibleCases() {

        Problem problem = new Problem();
        problem.setTitle("Repository Filtering Test");
        problem.setDescription("Testing visible and hidden cases");
        problem.setDifficulty(Difficulty.EASY);

        Problem savedProblem = problemRepository.saveAndFlush(problem);

        ProblemTestCase visibleCase = new ProblemTestCase();
        visibleCase.setInput("2 3");
        visibleCase.setExpectedOutput("5");
        visibleCase.setHidden(false);
        visibleCase.setProblem(savedProblem);

        ProblemTestCase hiddenCase = new ProblemTestCase();
        hiddenCase.setInput("100 200");
        hiddenCase.setExpectedOutput("300");
        hiddenCase.setHidden(true);
        hiddenCase.setProblem(savedProblem);

        problemTestCaseRepository.save(visibleCase);
        problemTestCaseRepository.save(hiddenCase);
        problemTestCaseRepository.flush();

        List<ProblemTestCase> result =
                problemTestCaseRepository
                        .findByProblemIdAndHiddenFalse(savedProblem.getId());

        assertEquals(1, result.size());
        assertEquals("2 3", result.get(0).getInput());
        assertFalse(result.get(0).isHidden());
        assertTrue(result.stream().noneMatch(ProblemTestCase::isHidden));
    }
}
