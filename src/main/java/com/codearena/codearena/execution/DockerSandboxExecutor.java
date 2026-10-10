package com.codearena.codearena.execution;

import com.codearena.codearena.model.SandboxExecutionResult;
import com.codearena.codearena.model.SandboxLimits;
import com.codearena.codearena.model.SandboxErrorType;
import com.codearena.codearena.config.SandboxProperties;
import com.codearena.codearena.exception.SandboxTimeoutException;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectExecResponse;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import com.github.dockerjava.api.model.HostConfig;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import com.github.dockerjava.core.command.ExecStartResultCallback;

import com.codearena.codearena.model.ExecCommandResult;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import java.util.concurrent.TimeUnit;

@Component
@Primary
public class DockerSandboxExecutor implements SandboxExecutor {

    private final DockerClient dockerClient;
    private final SandboxLimits sandboxLimits;
    private final SandboxProperties sandboxProperties;

    public DockerSandboxExecutor(
            DockerClient dockerClient,
            SandboxLimits sandboxLimits,
            SandboxProperties sandboxProperties) {

        this.dockerClient = dockerClient;
        this.sandboxLimits = sandboxLimits;
        this.sandboxProperties = sandboxProperties;
    }

@Override
public SandboxExecutionResult execute(
        String sourceCode,
        String input) {

    String containerId = null;

    try {

        ensureImageAvailable();

        containerId = createContainer();

        startContainer(containerId);

        copySourceCodeToContainer(
                containerId,
                sourceCode
        );

        copyInputToContainer(
                containerId,
                input
        );
        
      
ExecCommandResult compilationResult =
        compileSourceCode(containerId);

if (compilationResult.getExitCode() == null) {
    return new SandboxExecutionResult(
            false,
            "",
            "Compilation exit code is unavailable",
            0L,
            0L,
            SandboxErrorType.COMPILATION_ERROR
    );
}

if (compilationResult.getExitCode() != 0) {
    return new SandboxExecutionResult(
            false,
            "",
            compilationResult.getStderr(),
            0L,
            0L,
            SandboxErrorType.COMPILATION_ERROR
    );
}

ExecCommandResult executionResult =
        executeCompiledCode(containerId);

if (executionResult.getExitCode() == null) {
    return new SandboxExecutionResult(
            false,
            executionResult.getStdout(),
            "Execution exit code is unavailable",
            0L,
            0L,
            SandboxErrorType.RUNTIME_ERROR
    );
}

boolean success = executionResult.getExitCode() == 0;

return new SandboxExecutionResult(
        success,
        executionResult.getStdout(),
        success ? null : executionResult.getStderr(),
        0L,
        0L,
        success
                ? SandboxErrorType.NONE
                : SandboxErrorType.RUNTIME_ERROR
);

        throw new UnsupportedOperationException(
                "Docker compilation and execution are not implemented yet"
        );

    } finally {

        if (containerId != null) {
            removeContainer(containerId);
        }
    }
}

    private void ensureImageAvailable() {

    String image = sandboxProperties.getDockerImage();

    try {

        dockerClient
                .inspectImageCmd(image)
                .exec();

    } catch (com.github.dockerjava.api.exception.NotFoundException exception) {

        try {

            dockerClient
                    .pullImageCmd(image)
                    .start()
                    .awaitCompletion();

        } catch (InterruptedException interruptedException) {

            Thread.currentThread().interrupt();

            throw new IllegalStateException(
                    "Docker image pull was interrupted",
                    interruptedException
            );
        }
    }
}

private String createContainer() {

    String image = sandboxProperties.getDockerImage();

    return dockerClient
        .createContainerCmd(image)
        .withCmd("sleep", "infinity")
        .withHostConfig(
                new HostConfig()
                        .withMemory(
                                sandboxLimits.getMemoryLimitMb()
                                        * 1024L
                                        * 1024L
                        )
                        .withNetworkMode("none")
        )
        .withUser("1000:1000")
        .exec()
        .getId();
}

private void startContainer(String containerId) {

    dockerClient
            .startContainerCmd(containerId)
            .exec();
}

private void copySourceCodeToContainer(
        String containerId,
        String sourceCode,
        String input) {

    byte[] archive = createSourceArchive(sourceCode, input);

    dockerClient
            .copyArchiveToContainerCmd(containerId)
            .withRemotePath("/tmp")
            .withTarInputStream(
                    new java.io.ByteArrayInputStream(archive)
            )
            .exec();
}

private byte[] createSourceArchive(String sourceCode, String input) {

    try {
        ByteArrayOutputStream outputStream =
                new ByteArrayOutputStream();

        TarArchiveOutputStream tarOutputStream =
                new TarArchiveOutputStream(outputStream);

        byte[] sourceBytes =
                sourceCode.getBytes(StandardCharsets.UTF_8);

        TarArchiveEntry entry =
                new TarArchiveEntry("Main.java");

        entry.setSize(sourceBytes.length);

        tarOutputStream.putArchiveEntry(entry);

        tarOutputStream.write(sourceBytes);

        tarOutputStream.closeArchiveEntry();

        byte[] inputBytes =
                (input == null ? "" : input).getBytes(StandardCharsets.UTF_8);
        TarArchiveEntry inputEntry =
                new TarArchiveEntry("input.txt");
        inputEntry.setSize(inputBytes.length);
        tarOutputStream.putArchiveEntry(inputEntry);
        tarOutputStream.write(inputBytes);
        tarOutputStream.closeArchiveEntry();

        tarOutputStream.finish();
        tarOutputStream.close();

        return outputStream.toByteArray();

    } catch (IOException exception) {

        throw new IllegalStateException(
                "Failed to create source archive",
                exception
        );
    }
}

private ExecCommandResult compileSourceCode(
        String containerId) {

    ExecCreateCmdResponse execResponse =
            dockerClient
                    .execCreateCmd(containerId)
                    .withCmd(
                            "sh",
                            "-c",
                            "cd /tmp && javac Main.java"
                    )
                    .exec();

    ByteArrayOutputStream stdout =
            new ByteArrayOutputStream();

    ByteArrayOutputStream stderr =
            new ByteArrayOutputStream();

    try {

        dockerClient
                .execStartCmd(execResponse.getId())
                .exec(
                        new ExecStartResultCallback(
                                stdout,
                                stderr
                        )
                )
                .awaitCompletion(5, TimeUnit.SECONDS);

        InspectExecResponse inspectResponse =
                dockerClient
                        .inspectExecCmd(execResponse.getId())
                        .exec();

        return new ExecCommandResult(
                inspectResponse.getExitCode() == null
                        ? null
                        : inspectResponse.getExitCode().longValue(),
                stdout.toString(StandardCharsets.UTF_8),
                stderr.toString(StandardCharsets.UTF_8)
        );

    } catch (InterruptedException exception) {

        Thread.currentThread().interrupt();

        throw new IllegalStateException(
                "Compilation was interrupted",
                exception
        );
    }
}

private ExecCommandResult executeCompiledCode(
        String containerId) {

    String command =
            "cd /tmp && java Main < input.txt";

    ExecCreateCmdResponse execResponse =
            dockerClient
                    .execCreateCmd(containerId)
                    .withCmd("sh", "-c", command)
                    .exec();

    ByteArrayOutputStream stdout =
            new ByteArrayOutputStream();

    ByteArrayOutputStream stderr =
            new ByteArrayOutputStream();

    try {

     ExecStartResultCallback callback =
        new ExecStartResultCallback(stdout, stderr);

boolean completed =
        dockerClient
                .execStartCmd(execResponse.getId())
                .exec(callback)
                .awaitCompletion(
                        sandboxLimits.getTimeoutMillis(),
                        TimeUnit.MILLISECONDS
                );

if (!completed) {
    throw new SandboxTimeoutException("Execution timed out");
}
        InspectExecResponse inspectResponse =
                dockerClient
                        .inspectExecCmd(execResponse.getId())
                        .exec();

        return new ExecCommandResult(
                inspectResponse.getExitCode() == null
                        ? null
                        : inspectResponse.getExitCode().longValue(),
                stdout.toString(StandardCharsets.UTF_8),
                stderr.toString(StandardCharsets.UTF_8)
        );

    } catch (InterruptedException exception) {

        Thread.currentThread().interrupt();

        throw new IllegalStateException(
                "Code execution was interrupted",
                exception
        );
    }
}

private void copyInputToContainer(
        String containerId,
        String input) {

    byte[] archive = createInputArchive(input);

    dockerClient
            .copyArchiveToContainerCmd(containerId)
            .withRemotePath("/tmp")
            .withTarInputStream(
                    new ByteArrayInputStream(archive)
            )
            .exec();
}

private byte[] createInputArchive(String input) {

    try {
        ByteArrayOutputStream outputStream =
                new ByteArrayOutputStream();

        TarArchiveOutputStream tarOutputStream =
                new TarArchiveOutputStream(outputStream);

        byte[] inputBytes =
                (input == null ? "" : input)
                        .getBytes(StandardCharsets.UTF_8);

        TarArchiveEntry entry =
                new TarArchiveEntry("input.txt");

        entry.setSize(inputBytes.length);

        tarOutputStream.putArchiveEntry(entry);
        tarOutputStream.write(inputBytes);
        tarOutputStream.closeArchiveEntry();

        tarOutputStream.finish();
        tarOutputStream.close();

        return outputStream.toByteArray();

    } catch (IOException exception) {

        throw new IllegalStateException(
                "Failed to create input archive",
                exception
        );
    }
}

private void removeContainer(String containerId) {

    try {
        dockerClient
                .removeContainerCmd(containerId)
                .withForce(true)
                .exec();

    } catch (Exception exception) {

        // Cleanup failure ko original execution error
        // ko hide nahi karna chahiye.
    }
}

private void stopContainer(String containerId) {
    try {
        dockerClient
                .stopContainerCmd(containerId)
                .withTimeout(1)
                .exec();

    } catch (Exception exception) {
        // Cleanup failure should not hide the original error.
    }
}

}