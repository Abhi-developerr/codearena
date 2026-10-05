package com.codearena.codearena.execution;

import com.codearena.codearena.model.SandboxExecutionResult;
import org.springframework.stereotype.Component;
import com.codearena.codearena.model.SandboxErrorType;

@Component
public class DummySandboxExecutor implements SandboxExecutor {

    @Override
    public SandboxExecutionResult execute(
            String sourceCode,
            String input) {

        return new SandboxExecutionResult(
                false,
                "",
                "Sandbox execution is not implemented yet",
                0L,
                0L,
                SandboxErrorType.NONE
        );
    }
}