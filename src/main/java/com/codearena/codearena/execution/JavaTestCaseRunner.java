package com.codearena.codearena.execution;

import com.codearena.codearena.entity.ProblemTestCase;
import com.codearena.codearena.model.ExecutionRequest;
import com.codearena.codearena.model.TestCaseExecutionResult;
import org.springframework.stereotype.Component;

@Component
public class JavaTestCaseRunner implements TestCaseRunner {

    @Override
    public TestCaseExecutionResult run(
            ExecutionRequest request,
            ProblemTestCase testCase) {

        throw new UnsupportedOperationException(
                "Java execution is not implemented yet"
        );
    }
}