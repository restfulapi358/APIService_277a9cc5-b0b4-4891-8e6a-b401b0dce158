package com.eduelite.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication(scanBasePackages = {"com.eduelite.common.*", "com.eduelite.api.*"})
@EnableKafka
public class EdueliteServiceApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(EdueliteServiceApiApplication.class, args);
	}

}
