package com.heavenstay.utils;

import com.heavenstay.filehandler.BookingFileHandler;
import com.heavenstay.filehandler.PaymentFileHandler;
import com.heavenstay.filehandler.ReviewFileHandler;
import com.heavenstay.filehandler.UserFileHandler;

public class IDGenerator {

    public static String generateUserId() {
        int count = UserFileHandler.loadAllUsers().size() + 1;
        return String.format("U%03d", count);
    }

    public static String generateBookingId() {
        int count = BookingFileHandler.loadAllBookings().size() + 1;
        return String.format("B%03d", count);
    }

    public static String generatePaymentId() {
        int count = PaymentFileHandler.loadAllPayments().size() + 1;
        return String.format("P%03d", count);
    }

    public static String generateReviewId() {
        int count = ReviewFileHandler.loadAllReviews().size() + 1;
        return String.format("RV%03d", count);
    }
}