package com.codearena.codearena.config;

import com.codearena.codearena.model.SandboxLimits;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SandboxConfig {

    @Bean
    public SandboxLimits sandboxLimits(
            SandboxProperties properties) {

        return properties.toLimits();
    }
}