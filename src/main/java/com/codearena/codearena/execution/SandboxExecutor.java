package com.codearena.codearena.execution;

import com.codearena.codearena.model.SandboxExecutionResult;

public interface SandboxExecutor {

    SandboxExecutionResult execute(
            String sourceCode,
            String input
    );
}