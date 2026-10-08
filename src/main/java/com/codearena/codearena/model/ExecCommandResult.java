package com.codearena.codearena.model;

import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.command.InspectExecResponse;

public class ExecCommandResult {

    private final Long exitCode;
    private final String stdout;
    private final String stderr;

    public ExecCommandResult(
            Long exitCode,
            String stdout,
            String stderr) {

        this.exitCode = exitCode;
        this.stdout = stdout;
        this.stderr = stderr;
    }

    public Long getExitCode() {
        return exitCode;
    }

    public String getStdout() {
        return stdout;
    }

    public String getStderr() {
        return stderr;
    }
}