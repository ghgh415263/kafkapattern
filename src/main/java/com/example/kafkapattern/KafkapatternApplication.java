package com.example.kafkapattern;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class KafkapatternApplication {

	public static void main(String[] args) {
		SpringApplication.run(KafkapatternApplication.class, args);
	}

}
