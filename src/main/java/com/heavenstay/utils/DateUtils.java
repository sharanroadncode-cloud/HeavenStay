package com.heavenstay.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

public class DateUtils {

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static String today() {
        return LocalDate.now().format(FMT);
    }

    public static boolean isValidDate(String date) {
        if (date == null || date.trim().isEmpty()) return false;
        try {
            LocalDate.parse(date.trim(), FMT);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static boolean isFutureOrToday(String date) {
        if (!isValidDate(date)) return false;
        try {
            LocalDate d = LocalDate.parse(date.trim(), FMT);
            return !d.isBefore(LocalDate.now());
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isAfter(String laterDate, String earlierDate) {
        if (!isValidDate(laterDate) || !isValidDate(earlierDate)) return false;
        try {
            LocalDate later   = LocalDate.parse(laterDate.trim(), FMT);
            LocalDate earlier = LocalDate.parse(earlierDate.trim(), FMT);
            return later.isAfter(earlier);
        } catch (Exception e) {
            return false;
        }
    }

    public static int calculateNights(String checkIn, String checkOut) {
        if (!isValidDate(checkIn) || !isValidDate(checkOut)) return 0;
        try {
            LocalDate in  = LocalDate.parse(checkIn.trim(), FMT);
            LocalDate out = LocalDate.parse(checkOut.trim(), FMT);
            int nights = (int) ChronoUnit.DAYS.between(in, out);
            return nights > 0 ? nights : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    public static LocalDate parse(String date) {
        if (!isValidDate(date)) return null;
        try {
            return LocalDate.parse(date.trim(), FMT);
        } catch (Exception e) {
            return null;
        }
    }

    public static String format(LocalDate date) {
        if (date == null) return "";
        return date.format(FMT);
    }
}