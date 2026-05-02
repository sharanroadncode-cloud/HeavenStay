package com.srh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Serendib Royal Hotel – Reception Management System
 * Main entry point for the Spring Boot application.
 *
 * NOTE: Replace the auto-generated SrhApplication.java with this file,
 * OR simply paste the contents of this file into your existing SrhApplication.java
 */
@SpringBootApplication
public class SrhApplication {

    public static void main(String[] args) {
        SpringApplication.run(SrhApplication.class, args);
        System.out.println("=========================================");
        System.out.println("  Serendib Royal Hotel System Started!");
        System.out.println("  Dashboard: http://localhost:8080/dashboard.html");
        System.out.println("=========================================");
    }
}
