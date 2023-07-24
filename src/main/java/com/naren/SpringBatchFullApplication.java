package com.naren;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.naren")
@EnableBatchProcessing
public class SpringBatchFullApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBatchFullApplication.class, args);
	}

}
