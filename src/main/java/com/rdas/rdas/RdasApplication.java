package com.rdas.rdas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
@EnableScheduling
@SpringBootApplication
public class RdasApplication {

	public static void main(String[] args) {
		SpringApplication.run(RdasApplication.class, args);
	}

}
