
package com.codearena.codearena.service;

import com.codearena.codearena.dto.ProblemTestCasePublicResponse;
import com.codearena.codearena.entity.Problem;
import com.codearena.codearena.entity.ProblemTestCase;
import com.codearena.codearena.repository.ProblemRepository;
import com.codearena.codearena.repository.ProblemTestCaseRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProblemTestCaseServiceTest {

    @Mock
    private ProblemTestCaseRepository problemTestCaseRepository;

    @Mock
    private ProblemRepository problemRepository;

    @InjectMocks
    private ProblemTestCaseService problemTestCaseService;

    @Test
    void getSampleTestCasesReturnsOnlyNonHiddenCases() {

        Long problemId = 1L;

        Problem problem = new Problem();
        problem.setId(problemId);

        ProblemTestCase sampleCase = new ProblemTestCase();
        sampleCase.setId(10L);
        sampleCase.setInput("2 3");
        sampleCase.setExpectedOutput("5");
        sampleCase.setHidden(false);
        sampleCase.setProblem(problem);

        when(problemRepository.findById(problemId))
                .thenReturn(Optional.of(problem));

        when(problemTestCaseRepository
                .findByProblemIdAndHiddenFalse(problemId))
                .thenReturn(List.of(sampleCase));

        List<ProblemTestCasePublicResponse> result =
                problemTestCaseService.getSampleTestCases(problemId);

        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getId());
        assertEquals("2 3", result.get(0).getInput());
        assertEquals("5", result.get(0).getExpectedOutput());

        verify(problemTestCaseRepository)
                .findByProblemIdAndHiddenFalse(problemId);
    }
}
