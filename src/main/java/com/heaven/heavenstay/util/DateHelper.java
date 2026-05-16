package com.heaven.heavenstay.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateHelper {

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private DateHelper() {}

    public static String now() {
        return LocalDateTime.now().format(FORMAT);
    }

    public static String daysFromNow(int days) {
        return LocalDateTime.now().plusDays(days).format(FORMAT);
    }
}