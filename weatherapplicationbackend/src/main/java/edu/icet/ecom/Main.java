package edu.icet.ecom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class Main {
    public static void main(String[] args) {
        // Application entry point - Spring Boot will bootstrap the context and start embedded server
        SpringApplication.run(Main.class, args);
    }
}