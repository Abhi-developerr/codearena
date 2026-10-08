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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

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


}