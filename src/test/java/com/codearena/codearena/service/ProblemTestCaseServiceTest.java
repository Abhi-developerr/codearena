
package com.codearena.codearena.service;

import com.codearena.codearena.dto.ProblemTestCasePublicResponse;
import com.codearena.codearena.entity.Problem;
import com.codearena.codearena.entity.ProblemTestCase;
import com.codearena.codearena.repository.ProblemRepository;
import com.codearena.codearena.repository.ProblemTestCaseRepository;
import com.codearena.codearena.exception.ProblemTestCaseOwnershipException;
import com.codearena.codearena.exception.ProblemNotFoundException;
import com.codearena.codearena.dto.ProblemTestCaseRequest;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
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


@Test
void updateTestCaseRejectsTestCaseOwnedByAnotherProblem() {

    Long requestedProblemId = 1L;
    Long actualProblemId = 2L;
    Long testCaseId = 10L;

    Problem requestedProblem = new Problem();
    requestedProblem.setId(requestedProblemId);

    Problem actualProblem = new Problem();
    actualProblem.setId(actualProblemId);

    ProblemTestCase testCase = new ProblemTestCase();
    testCase.setId(testCaseId);
    testCase.setProblem(actualProblem);

    when(problemRepository.findById(requestedProblemId))
            .thenReturn(Optional.of(requestedProblem));

    when(problemTestCaseRepository.findById(testCaseId))
            .thenReturn(Optional.of(testCase));

    ProblemTestCaseRequest request = new ProblemTestCaseRequest();
    request.setInput("2 3");
    request.setExpectedOutput("5");

    assertThrows(
            ProblemTestCaseOwnershipException.class,
            () -> problemTestCaseService.updateTestCase(
                    requestedProblemId, testCaseId, request)
    );

    verify(problemTestCaseRepository, never()).save(testCase);
}

@Test
void deleteTestCaseRejectsTestCaseOwnedByAnotherProblem() {

    Long requestedProblemId = 1L;
    Long actualProblemId = 2L;
    Long testCaseId = 10L;

    Problem requestedProblem = new Problem();
    requestedProblem.setId(requestedProblemId);

    Problem actualProblem = new Problem();
    actualProblem.setId(actualProblemId);

    ProblemTestCase testCase = new ProblemTestCase();
    testCase.setId(testCaseId);
    testCase.setProblem(actualProblem);

    when(problemRepository.findById(requestedProblemId))
            .thenReturn(Optional.of(requestedProblem));

    when(problemTestCaseRepository.findById(testCaseId))
            .thenReturn(Optional.of(testCase));

    assertThrows(
            ProblemTestCaseOwnershipException.class,
            () -> problemTestCaseService.deleteTestCase(
                    requestedProblemId, testCaseId)
    );

    verify(problemTestCaseRepository, never()).delete(testCase);
}

}
