package com.codearena.codearena.execution;

import com.codearena.codearena.model.SandboxExecutionResult;
import com.codearena.codearena.model.SandboxLimits;
import org.springframework.stereotype.Component;

@Component
public class DockerSandboxExecutor implements SandboxExecutor {

    private final SandboxLimits sandboxLimits;

    public DockerSandboxExecutor(
            SandboxLimits sandboxLimits) {

        this.sandboxLimits = sandboxLimits;
    }

    @Override
    public SandboxExecutionResult execute(
            String sourceCode,
            String input) {

        throw new UnsupportedOperationException(
                "Docker sandbox execution is not implemented yet"
        );
    }
}