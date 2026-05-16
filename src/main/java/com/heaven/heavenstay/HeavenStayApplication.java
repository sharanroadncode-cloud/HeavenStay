package com.heaven.heavenstay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HeavenStayApplication {

    public static void main(String[] args) {
        SpringApplication.run(HeavenStayApplication.class, args);
        System.out.println("Haven Stay Hotel - Reception System Started on port 8080");
    }
}