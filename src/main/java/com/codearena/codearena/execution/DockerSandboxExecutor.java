package com.codearena.codearena.execution;

import com.codearena.codearena.model.SandboxExecutionResult;
import com.codearena.codearena.model.SandboxLimits;
import com.codearena.codearena.config.SandboxProperties;
import com.github.dockerjava.api.DockerClient;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import com.github.dockerjava.api.model.HostConfig;

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

private String createWriteSourceExec(
        String containerId,
        String sourceCode) {

    String command =
            "printf '%s' " +
            shellQuote(sourceCode) +
            " > /tmp/Main.java";

    return dockerClient
            .execCreateCmd(containerId)
            .withCmd("sh", "-c", command)
            .exec()
            .getId();
}

private String shellQuote(String value) {

    return "'" + value.replace("'", "'\\''") + "'";
}

}