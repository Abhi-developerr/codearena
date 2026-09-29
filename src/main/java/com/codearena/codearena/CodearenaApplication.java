package com.codearena.codearena;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CodearenaApplication {

	public static void main(String[] args) {
		SpringApplication.run(CodearenaApplication.class, args);
	}

}
