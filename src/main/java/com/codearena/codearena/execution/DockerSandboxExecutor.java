package com.codearena.codearena.execution;

import com.codearena.codearena.model.SandboxExecutionResult;
import com.codearena.codearena.model.SandboxLimits;
import com.codearena.codearena.model.SandboxErrorType;
import com.codearena.codearena.config.SandboxProperties;
import com.codearena.codearena.exception.SandboxTimeoutException;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.model.Statistics;
import java.util.concurrent.atomic.AtomicLong;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codearena.codearena.model.ExecCommandResult;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import java.util.concurrent.TimeUnit;

@Component
@Primary
@SuppressWarnings("deprecation")
public class DockerSandboxExecutor implements SandboxExecutor {

    private static final Logger logger =
            LoggerFactory.getLogger(DockerSandboxExecutor.class);

    private final DockerClient dockerClient;
    private final SandboxLimits sandboxLimits;
    private final SandboxProperties sandboxProperties;

    private long toMegabytes(long bytes) {
    return bytes / (1024L * 1024L);
}

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
        
      long startTime = System.nanoTime();
ExecCommandResult compilationResult =
      compileSourceCode(containerId);

long executionTimeMillis =
      elapsedMillis(startTime);

if (compilationResult.getExitCode() == null) {
    return new SandboxExecutionResult(
            false,
            "",
            "Compilation exit code is unavailable",
            executionTimeMillis,
            0L,
            SandboxErrorType.COMPILATION_ERROR
    );
}

if (compilationResult.getExitCode() != 0) {
    SandboxErrorType errorType =
            compilationResult.getExitCode() == 124L
                    ? SandboxErrorType.TIME_LIMIT_EXCEEDED
                    : SandboxErrorType.COMPILATION_ERROR;

    return new SandboxExecutionResult(
            false,
            "",
            compilationResult.getStderr(),
            executionTimeMillis,
            0L,
            errorType
    );
}

try {
    AtomicLong peakMemoryBytes = new AtomicLong(0L);
    ResultCallback.Adapter<Statistics> memoryCallback =
            startMemoryMonitoring(containerId, peakMemoryBytes);
    ExecCommandResult executionResult;

    try {
        executionResult = executeCompiledCode(containerId);
    } finally {
        try {
            memoryCallback.close();
        } catch (Exception ignored) {
            // Do not hide the original execution result.
        }
    }

    if (executionResult.getExitCode() == null) {
        return new SandboxExecutionResult(
                false,
                executionResult.getStdout(),
                "Execution exit code is unavailable",
                elapsedMillis(startTime),
                toMegabytes(peakMemoryBytes.get()),
                SandboxErrorType.RUNTIME_ERROR
        );
    }

    boolean success =
            executionResult.getExitCode() == 0;

    return new SandboxExecutionResult(
            success,
            executionResult.getStdout(),
            success ? null : executionResult.getStderr(),
            elapsedMillis(startTime),
            toMegabytes(peakMemoryBytes.get()),
            success
                    ? SandboxErrorType.NONE
                    : SandboxErrorType.RUNTIME_ERROR
    );

} catch (SandboxTimeoutException exception) {
    return new SandboxExecutionResult(
            false,
            "",
            exception.getMessage(),
            elapsedMillis(startTime),
            0L,
            SandboxErrorType.TIME_LIMIT_EXCEEDED
    );
}

    } finally {

        if (containerId != null) {
            removeContainer(containerId);
        }
    }
}

private boolean wasContainerOomKilled(String containerId) {
    try {
        var containerInfo = dockerClient
                .inspectContainerCmd(containerId)
                .exec();

        return containerInfo.getState() != null
                && Boolean.TRUE.equals(
                        containerInfo.getState().getOOMKilled()
                );

    } catch (Exception exception) {
        // Inspection failure must not hide the original result.
        return false;
    }
}

private void updatePeakMemory(
        Statistics statistics,
        AtomicLong peakMemoryBytes) {

    if (statistics == null || statistics.getMemoryStats() == null) {
        return;
    }

    Long usageBytes = statistics.getMemoryStats().getUsage();

    if (usageBytes != null) {

        peakMemoryBytes.updateAndGet(
                currentPeak -> Math.max(currentPeak, usageBytes)
        );
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
                        .withPidsLimit(256L)
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
        String sourceCode) {

    byte[] archive = createSourceArchive(sourceCode);

    dockerClient
            .copyArchiveToContainerCmd(containerId)
            .withRemotePath("/tmp")
            .withTarInputStream(
                    new java.io.ByteArrayInputStream(archive)
            )
            .exec();
}

private byte[] createSourceArchive(String sourceCode) {

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

       
boolean completed =
        dockerClient
                .execStartCmd(execResponse.getId())
                .exec(
                        new ExecStartResultCallback(
                                stdout,
                                stderr
                        )
                )
                .awaitCompletion(
                        sandboxLimits.getTimeoutMillis(),
                        TimeUnit.MILLISECONDS
                );

if (!completed) {
    stopContainer(containerId);

    return new ExecCommandResult(
            124L,
            "",
            "Compilation timed out"
    );
}


        InspectExecResponse inspectResponse =
                dockerClient
                        .inspectExecCmd(execResponse.getId())
                        .exec();

        return new ExecCommandResult(
                inspectResponse.getExitCodeLong() == null
                        ? null
                        : inspectResponse.getExitCodeLong(),
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
    stopContainer(containerId);

    throw new SandboxTimeoutException(
            "Execution timed out"
    );
}
    InspectExecResponse inspectResponse =
                dockerClient
                        .inspectExecCmd(execResponse.getId())
                        .exec();

        return new ExecCommandResult(
                inspectResponse.getExitCodeLong() == null
                        ? null
                        : inspectResponse.getExitCodeLong(),
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
        logger.warn(
                "Failed to remove sandbox container {}",
                containerId,
                exception
        );
    }
}

private void stopContainer(String containerId) {
    try {
        dockerClient
                .stopContainerCmd(containerId)
                .withTimeout(1)
                .exec();

    } catch (Exception exception) {
        logger.warn(
                "Failed to stop sandbox container {}",
                containerId,
                exception
        );
    }
}

private long elapsedMillis(long startTimeNanos) {
    return TimeUnit.NANOSECONDS.toMillis(
            System.nanoTime() - startTimeNanos
    );
}

private com.github.dockerjava.api.async.ResultCallback.Adapter<
        com.github.dockerjava.api.model.Statistics> startMemoryMonitoring(
        String containerId,
        AtomicLong peakMemoryBytes) {

    var callback =
            new com.github.dockerjava.api.async.ResultCallback.Adapter<
                    com.github.dockerjava.api.model.Statistics>() {

                @Override
                public void onNext(
                        com.github.dockerjava.api.model.Statistics statistics) {

                    updatePeakMemory(statistics, peakMemoryBytes);
                }
            };

    dockerClient.statsCmd(containerId).exec(callback);

    return callback;
}



}