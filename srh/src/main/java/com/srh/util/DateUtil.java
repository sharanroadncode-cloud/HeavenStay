package com.srh.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * DateUtil – helper for consistent date/time formatting across the system.
 */
public class DateUtil {

    /** Standard display format: yyyy-MM-dd HH:mm:ss */
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Private constructor – static utility class
    private DateUtil() {}

    /**
     * Returns the current date and time as a formatted string.
     * @return e.g. "2024-06-15 14:30:00"
     */
    public static String now() {
        return LocalDateTime.now().format(FORMATTER);
    }

    /**
     * Returns a date string that is N days ahead of now.
     * Used for keycard expiry calculations.
     *
     * @param days number of days to add
     * @return formatted future date string
     */
    public static String daysFromNow(int days) {
        return LocalDateTime.now().plusDays(days).format(FORMATTER);
    }
}
