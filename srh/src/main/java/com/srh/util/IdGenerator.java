package com.srh.util;

public class IdGenerator {

    private IdGenerator() {}

    public static String generateCheckInId() {
        return "CI" + System.currentTimeMillis();
    }

    public static String generateCheckOutId() {
        return "CO" + System.currentTimeMillis();
    }

    public static String generateKeyCardId() {
        return "KC" + System.currentTimeMillis();
    }
}