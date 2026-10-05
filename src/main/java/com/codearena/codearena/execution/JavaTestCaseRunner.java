package com.codearena.codearena.execution;

import com.codearena.codearena.entity.ProblemTestCase;
import com.codearena.codearena.model.ExecutionRequest;
import com.codearena.codearena.model.TestCaseExecutionResult;
import com.codearena.codearena.model.SandboxExecutionResult;
import com.codearena.codearena.model.SandboxErrorType;
import org.springframework.stereotype.Component;

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

    SandboxExecutionResult sandboxResult =
            sandboxExecutor.execute(
                    request.getSourceCode(),
                    testCase.getInput()
            );

    boolean passed =
            sandboxResult.getErrorType() == SandboxErrorType.NONE
            && sandboxResult.isSuccess()
            && sandboxResult.getOutput()
                    .trim()
                    .equals(testCase.getExpectedOutput().trim());

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
}