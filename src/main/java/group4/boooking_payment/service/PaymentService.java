package group4.boooking_payment.service;

import group4.boooking_payment.model.Booking;
import group4.boooking_payment.model.Payment;
import group4.boooking_payment.repository.BookingRepository;
import group4.boooking_payment.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Service

public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private BookingRepository bookingRepository;

    // ─── Process Payment ──────────────────────────────────────────────────────

    public Payment processPayment(Payment payment) {
        // Block duplicate payment for same booking
        if (paymentRepository.existsByBookingIdAndStatus(payment.getBookingId(), "COMPLETED")) {
            throw new RuntimeException("Booking " + payment.getBookingId() + " is already paid.");
        }

        // Validate booking exists
        Booking booking = bookingRepository.findById(payment.getBookingId())
                .orElseThrow(() -> new RuntimeException(
                        "Booking not found: " + payment.getBookingId()));

        // Generate unique payment ID
        String paymentId = "PY-" + UUID.randomUUID()
                .toString().replace("-", "").substring(0, 6).toUpperCase(Locale.ROOT);
        payment.setPaymentId(paymentId);
        payment.setStatus("COMPLETED");

        // Default transaction date to today if not supplied
        if (payment.getTransactionDate() == null || payment.getTransactionDate().isEmpty()) {
            payment.setTransactionDate(java.time.LocalDate.now().toString());
        }

        // Auto-confirm booking when payment is received
        if ("PENDING".equals(booking.getStatus())) {
            booking.setStatus("CONFIRMED");
            bookingRepository.save(booking);
        }

        return paymentRepository.save(payment);
    }

    // ─── Get All Payments ─────────────────────────────────────────────────────

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    // ─── Get Payment by ID ────────────────────────────────────────────────────

    public Optional<Payment> getPaymentById(String paymentId) {
        return paymentRepository.findById(paymentId);
    }

    // ─── Get Payment by Booking ID ────────────────────────────────────────────

    public Optional<Payment> getPaymentByBookingId(String bookingId) {
        return paymentRepository.findByBookingId(bookingId);
    }

    // ─── Refund Payment ───────────────────────────────────────────────────────

    public Payment refundPayment(String paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found: " + paymentId));

        if (!"COMPLETED".equals(payment.getStatus())) {
            throw new RuntimeException("Only completed payments can be refunded.");
        }

        payment.refund();

        // Cancel the linked booking
        bookingRepository.findById(payment.getBookingId()).ifPresent(b -> {
            b.setStatus("CANCELLED");
            bookingRepository.save(b);
        });

        return paymentRepository.save(payment);
    }

    // ─── Generate Invoice Number ──────────────────────────────────────────────

    public String generateInvoice(String paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found: " + paymentId));
        return payment.generateInvoice();
    }

    // ─── Delete Payment ───────────────────────────────────────────────────────

    public void deletePayment(String paymentId) {
        paymentRepository.deleteById(paymentId);
    }

}
