package com.heaven.heavenstay.util;

public class IdGenerator {

    private IdGenerator() {}

    public static String checkInId() {
        return "CI" + System.currentTimeMillis();
    }

    public static String checkOutId() {
        return "CO" + System.currentTimeMillis();
    }

    public static String keyCardId() {
        return "KC" + System.currentTimeMillis();
    }
}