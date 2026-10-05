package com.codearena.codearena.execution;

import com.codearena.codearena.entity.ProblemTestCase;
import com.codearena.codearena.model.ExecutionRequest;
import com.codearena.codearena.model.
TestCaseExecutionResult;
import com.codearena.codearena.model.SandboxErrorType;

public interface TestCaseRunner {

    @Override
        public TestCaseExecutionResult run(
        ExecutionRequest request,
        ProblemTestCase testCase) {

    SandboxExecutionResult sandboxResult =
            sandboxExecutor.execute(
                    request.getSourceCode(),
                    testCase.getInput()
            );

    boolean passed = false;

    if (sandboxResult.getErrorType() == SandboxErrorType.NONE) {

        passed = sandboxResult.isSuccess()
                && sandboxResult.getOutput()
                        .trim()
                        .equals(
                            testCase.getExpectedOutput().trim()
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
}