package com.codearena.codearena.execution;

import com.codearena.codearena.entity.ProblemTestCase;
import com.codearena.codearena.model.ExecutionRequest;
import com.codearena.codearena.model.TestCaseExecutionResult;

public interface TestCaseRunner {

    TestCaseExecutionResult run(
            ExecutionRequest request,
            ProblemTestCase testCase
    );
}