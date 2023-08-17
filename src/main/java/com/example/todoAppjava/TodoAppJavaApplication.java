package com.example.todoAppjava;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.example.todoAppjava")
public class TodoAppJavaApplication {

	public static void main(String[] args) {
		SpringApplication.run(TodoAppJavaApplication.class, args);
	}

}
