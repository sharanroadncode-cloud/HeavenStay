package com.srh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SrhApplication {

    public static void main(String[] args) {
        SpringApplication.run(SrhApplication.class, args);
        System.out.println("Haven Stay Hotel System Started!");
        System.out.println("Dashboard: http://localhost:8080/dashboard.html");
    }
}