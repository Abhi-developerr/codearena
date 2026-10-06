package com.codearena.codearena.config;

import com.codearena.codearena.model.SandboxLimits;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class SandboxConfig {

    @Bean
    @Primary
    public SandboxLimits sandboxLimits(
            SandboxProperties properties) {

        return properties.toLimits();
    }
}