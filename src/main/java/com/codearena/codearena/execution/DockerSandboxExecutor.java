package com.codearena.codearena.execution;

import com.codearena.codearena.model.SandboxExecutionResult;
import com.codearena.codearena.model.SandboxLimits;
import com.codearena.codearena.config.SandboxProperties;
import com.github.dockerjava.api.DockerClient;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.command.CopyArchiveToContainerCmd;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import com.github.dockerjava.api.model.ArchiveEntry;
import com.github.dockerjava.core.command.BuildImageResultCallback;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.ExecStartResultCallback;

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

        throw new UnsupportedOperationException(
                "Docker sandbox execution is not implemented yet"
        );
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

private String shellQuote(String value) {

    return "'" + value.replace("'", "'\\''") + "'";
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

private String readSourceFileFromContainer(String containerId) {

    ExecCreateCmdResponse execResponse =
            dockerClient
                    .execCreateCmd(containerId)
                    .withCmd("sh", "-c", "cat /tmp/Main.java")
                    .exec();

    dockerClient
            .execStartCmd(execResponse.getId())
            .exec();

    return execResponse.getId();
}

private String readSourceFileFromContainer(
        String containerId) {

    ExecCreateCmdResponse execResponse =
            dockerClient
                    .execCreateCmd(containerId)
                    .withCmd("sh", "-c", "cat /tmp/Main.java")
                    .exec();

    ByteArrayOutputStream outputStream =
            new ByteArrayOutputStream();

    try {

        dockerClient
                .execStartCmd(execResponse.getId())
                .exec(
                    new ExecStartResultCallback(
                            outputStream,
                            outputStream
                    )
                )
                .awaitCompletion(5, TimeUnit.SECONDS);

        return outputStream.toString(
                StandardCharsets.UTF_8
        );

    } catch (InterruptedException exception) {

        Thread.currentThread().interrupt();

        throw new IllegalStateException(
                "Interrupted while reading source file",
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
                inspectResponse.getExitCode(),
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

private void validateCompilationResult(
        ExecCommandResult result) {

    if (result.getExitCode() == null) {
        throw new IllegalStateException(
                "Compilation exit code is unavailable"
        );
    }

    if (result.getExitCode() != 0) {

        throw new IllegalStateException(
                "Compilation failed: "
                        + result.getStderr()
        );
    }
}


}