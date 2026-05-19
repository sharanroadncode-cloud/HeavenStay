package com.heavenstay.filehandler;

import com.heavenstay.models.Payment;

import java.util.ArrayList;
import java.util.List;

public class PaymentFileHandler {

    private static final String FILE_PATH = "data/payments.txt";

    public static List<Payment> loadAllPayments() {
        List<String> lines = FileManager.readLines(FILE_PATH);
        List<Payment> payments = new ArrayList<>();
        for (String line : lines) {
            try {
                payments.add(Payment.fromFileString(line));
            } catch (Exception e) {
                System.out.println("[WARN] Skipping payment line: " + line);
            }
        }
        return payments;
    }

    public static boolean saveAllPayments(List<Payment> payments) {
        List<String> lines = new ArrayList<>();
        for (Payment p : payments) lines.add(p.toFileString());
        return FileManager.writeLines(FILE_PATH, lines);
    }

    public static boolean appendPayment(Payment payment) {
        if (payment == null) return false;
        return FileManager.appendLine(FILE_PATH, payment.toFileString());
    }

    public static Payment findById(String paymentId) {
        if (paymentId == null || paymentId.trim().isEmpty()) return null;
        for (Payment p : loadAllPayments()) {
            if (p.getPaymentId().equals(paymentId)) return p;
        }
        return null;
    }

    public static Payment findByBookingId(String bookingId) {
        if (bookingId == null || bookingId.trim().isEmpty()) return null;
        for (Payment p : loadAllPayments()) {
            if (p.getBookingId().equals(bookingId)) return p;
        }
        return null;
    }

    public static List<Payment> findByCustomerId(String customerId) {
        List<Payment> result = new ArrayList<>();
        if (customerId == null || customerId.trim().isEmpty()) return result;
        for (Payment p : loadAllPayments()) {
            if (p.getCustomerId().equals(customerId)) result.add(p);
        }
        return result;
    }

    public static boolean updatePayment(Payment updated) {
        if (updated == null) return false;
        List<Payment> payments = loadAllPayments();
        boolean found = false;
        for (int i = 0; i < payments.size(); i++) {
            if (payments.get(i).getPaymentId().equals(updated.getPaymentId())) {
                payments.set(i, updated);
                found = true;
                break;
            }
        }
        if (!found) return false;
        return saveAllPayments(payments);
    }
}