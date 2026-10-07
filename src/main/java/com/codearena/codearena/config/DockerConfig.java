package com.codearena.codearena.config;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.core.DockerClientImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DockerConfig {

    @Bean
        DockerClient dockerClient() {

    DefaultDockerClientConfig config =
            DefaultDockerClientConfig.createDefaultConfigBuilder()
                    .build();

    ApacheDockerHttpClient httpClient =
            new ApacheDockerHttpClient.Builder()
                    .dockerHost(config.getDockerHost())
                    .sslConfig(config.getSSLConfig())
                    .build();

    DockerClient dockerClient =
            DockerClientImpl.getInstance(
                    config,
                    httpClient
            );

    dockerClient.pingCmd().exec();

    return dockerClient;
}
}