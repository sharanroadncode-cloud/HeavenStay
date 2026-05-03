package com.srh.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private DateUtil() {}

    public static String now() {
        return LocalDateTime.now().format(FORMATTER);
    }

    public static String daysFromNow(int days) {
        return LocalDateTime.now().plusDays(days).format(FORMATTER);
    }
}