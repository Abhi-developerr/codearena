package com.codearena.codearena;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.codearena.codearena.config.SandboxProperties;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(SandboxProperties.class)
public class CodearenaApplication {

	public static void main(String[] args) {
		SpringApplication.run(CodearenaApplication.class, args);
	}

}
