package com.codearena.codearena.execution;

import org.springframework.stereotype.Component;

import com.codearena.codearena.entity.ProblemTestCase;
import com.codearena.codearena.entity.SubmissionLanguage;
import com.codearena.codearena.model.ExecutionRequest;
import com.codearena.codearena.model.SandboxErrorType;
import com.codearena.codearena.model.SandboxExecutionResult;
import com.codearena.codearena.model.TestCaseExecutionResult;

@Component
public class JavaTestCaseRunner implements TestCaseRunner {

    private final SandboxExecutor sandboxExecutor;

    public JavaTestCaseRunner(
            SandboxExecutor sandboxExecutor) {

        this.sandboxExecutor = sandboxExecutor;
    }

 @Override
public TestCaseExecutionResult run(
        ExecutionRequest request,
        ProblemTestCase testCase) {

    if (request.getLanguage() != SubmissionLanguage.JAVA) {
        throw new IllegalArgumentException(
                "JavaTestCaseRunner supports only Java"
        );
    }

    if (request.getSourceCode() == null
            || request.getSourceCode().isBlank()) {

        throw new IllegalArgumentException(
                "Source code cannot be empty"
        );
    }

    if (testCase == null) {
        throw new IllegalArgumentException(
                "Test case cannot be null"
        );
    }

    SandboxExecutionResult sandboxResult =
            sandboxExecutor.execute(
                    request.getSourceCode(),
                    testCase.getInput()
            );

    boolean passed = false;

    if (sandboxResult.getErrorType() == SandboxErrorType.NONE) {

        passed = sandboxResult.isSuccess()
                && isOutputMatching(
                        sandboxResult.getOutput(),
                        testCase.getExpectedOutput()
                );
    }

    return new TestCaseExecutionResult(
            testCase.getId(),
            passed,
            sandboxResult.getExecutionTime(),
            sandboxResult.getMemoryUsed(),
            sandboxResult.getOutput(),
            sandboxResult.getErrorMessage(),
            sandboxResult.getErrorType()
    );
}

private boolean isOutputMatching(
        String actualOutput,
        String expectedOutput) {

    String actual = actualOutput == null
            ? ""
            : actualOutput.trim();

    String expected = expectedOutput == null
            ? ""
            : expectedOutput.trim();

    return actual.equals(expected);
}

}