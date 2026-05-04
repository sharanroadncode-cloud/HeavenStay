package com.haven;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HavenStayApplication {

    public static void main(String[] args) {
        SpringApplication.run(HavenStayApplication.class, args);
        System.out.println("Haven Stay Hotel - Reception System Started!");
        System.out.println("Open: http://localhost:8080/dashboard.html");
    }
}