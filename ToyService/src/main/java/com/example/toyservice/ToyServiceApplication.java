package com.example.toyservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ToyServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ToyServiceApplication.class, args);
	}

}
