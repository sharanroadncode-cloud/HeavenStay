package com.heavenstay.service;

import com.heavenstay.filehandler.BookingFileHandler;
import com.heavenstay.filehandler.PaymentFileHandler;
import com.heavenstay.models.Booking;
import com.heavenstay.models.Payment;
import com.heavenstay.utils.DateUtils;
import com.heavenstay.utils.IDGenerator;
import com.heavenstay.utils.ValidationUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentService {

    public Payment processPayment(String bookingId, String customerId,
                                  String method) {
        if (!ValidationUtils.isNotEmpty(bookingId)) return null;
        if (!ValidationUtils.isNotEmpty(customerId)) return null;
        if (!ValidationUtils.isValidPaymentMethod(method)) return null;

        Booking booking = BookingFileHandler.findById(bookingId.trim());
        if (booking == null) return null;
        if (!booking.getCustomerId().equals(customerId.trim())) return null;
        if (!Booking.STATUS_CONFIRMED.equals(booking.getStatus())) return null;
        if (isAlreadyPaid(bookingId.trim())) return null;

        String id = IDGenerator.generatePaymentId();
        Payment payment = new Payment(
                id,
                bookingId.trim(),
                customerId.trim(),
                booking.getTotalPrice(),
                method.toUpperCase(),
                Payment.STATUS_PAID,
                DateUtils.today()
        );

        boolean saved = PaymentFileHandler.appendPayment(payment);
        return saved ? payment : null;
    }

    public boolean refundPayment(String bookingId) {
        if (!ValidationUtils.isNotEmpty(bookingId)) return false;

        List<Payment> payments = PaymentFileHandler.loadAllPayments();
        boolean found = false;

        for (Payment p : payments) {
            if (p.getBookingId().equals(bookingId.trim()) &&
                    Payment.STATUS_PAID.equals(p.getStatus())) {
                p.setStatus(Payment.STATUS_REFUNDED);
                found = true;
                break;
            }
        }

        if (!found) return false;
        return PaymentFileHandler.saveAllPayments(payments);
    }

    public Payment getPaymentByBooking(String bookingId) {
        if (!ValidationUtils.isNotEmpty(bookingId)) return null;
        return PaymentFileHandler.findByBookingId(bookingId.trim());
    }

    public List<Payment> getAllPayments() {
        return PaymentFileHandler.loadAllPayments();
    }

    public List<Payment> getPaymentsByCustomer(String customerId) {
        if (!ValidationUtils.isNotEmpty(customerId)) return new ArrayList<>();
        return PaymentFileHandler.findByCustomerId(customerId.trim());
    }

    public double getTotalRevenue() {
        double total = 0;
        for (Payment p : PaymentFileHandler.loadAllPayments()) {
            if (Payment.STATUS_PAID.equals(p.getStatus())) {
                total += p.getAmount();
            }
        }
        return total;
    }

    public boolean isAlreadyPaid(String bookingId) {
        if (!ValidationUtils.isNotEmpty(bookingId)) return false;
        Payment p = PaymentFileHandler.findByBookingId(bookingId.trim());
        return p != null && Payment.STATUS_PAID.equals(p.getStatus());
    }
}