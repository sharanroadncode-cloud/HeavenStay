package com.heavenstay.utils;

public class ValidationUtils {

    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) return false;
        return email.contains("@")
                && email.indexOf("@") < email.lastIndexOf(".")
                && email.length() >= 5;
    }

    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) return false;
        return phone.trim().matches("\\d{7,15}");
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static boolean isValidRoomType(String type) {
        if (type == null) return false;
        switch (type.toUpperCase()) {
            case "SINGLE":
            case "DOUBLE":
            case "SUITE":
            case "DELUXE":
                return true;
            default:
                return false;
        }
    }

    public static boolean isValidRoomStatus(String status) {
        if (status == null) return false;
        switch (status.toUpperCase()) {
            case "AVAILABLE":
            case "BOOKED":
            case "CLEANING":
            case "MAINTENANCE":
                return true;
            default:
                return false;
        }
    }

    public static boolean isValidPaymentMethod(String method) {
        if (method == null) return false;
        switch (method.toUpperCase()) {
            case "CASH":
            case "CARD":
            case "ONLINE":
                return true;
            default:
                return false;
        }
    }

    public static boolean isValidRating(int rating) {
        return rating >= 1 && rating <= 5;
    }

    public static String sanitize(String input) {
        if (input == null) return "";
        return input.trim().replace("|", "-");
    }

    public static double parseDouble(String value, double fallback) {
        if (value == null || value.trim().isEmpty()) return fallback;
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    public static int parseInt(String value, int fallback) {
        if (value == null || value.trim().isEmpty()) return fallback;
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }
}