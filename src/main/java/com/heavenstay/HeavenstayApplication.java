package com.heavenstay;

import com.heavenstay.filehandler.FileManager;
import com.heavenstay.service.RoomService;
import com.heavenstay.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class HeavenstayApplication {

    public static void main(String[] args) {
        initDataFiles();
        ApplicationContext ctx = SpringApplication.run(HeavenstayApplication.class, args);
        ctx.getBean(UserService.class).seedDefaultAdmin();
        ctx.getBean(RoomService.class).seedSampleRooms();
    }

    private static void initDataFiles() {
        new java.io.File("data").mkdirs();
        String[] files = {
                "data/users.txt",
                "data/rooms.txt",
                "data/bookings.txt",
                "data/payments.txt",
                "data/reviews.txt"
        };
        for (String f : files) FileManager.createIfNotExists(f);
        System.out.println("[OK] Data files ready.");
    }
}