package group4.boooking_payment.repository;

import group4.boooking_payment.model.Payment;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository

public class PaymentRepository {

    // ─── In-memory store (replaces the database) ──────────────────────────────
    private final Map<String, Payment> store = new HashMap<>();

    // Pre-loaded sample data so the app works immediately on first run
    public PaymentRepository() {
        seedData();
    }

    // ─── CRUD ─────────────────────────────────────────────────────────────────

    public Payment save(Payment payment) {
        store.put(payment.getPaymentId(), payment);
        return payment;
    }

    public Optional<Payment> findById(String paymentId) {
        return Optional.ofNullable(store.get(paymentId));
    }

    public List<Payment> findAll() {
        return new ArrayList<>(store.values());
    }

    public void deleteById(String paymentId) {
        store.remove(paymentId);
    }

    public boolean existsById(String paymentId) {
        return store.containsKey(paymentId);
    }

    // ─── Custom Queries (stream-based) ────────────────────────────────────────

    public Optional<Payment> findByBookingId(String bookingId) {
        return store.values().stream()
                .filter(p -> bookingId.equals(p.getBookingId()))
                .findFirst();
    }

    public List<Payment> findByStatus(String status) {
        return store.values().stream()
                .filter(p -> status.equals(p.getStatus()))
                .collect(Collectors.toList());
    }

    public boolean existsByBookingIdAndStatus(String bookingId, String status) {
        return store.values().stream()
                .anyMatch(p -> bookingId.equals(p.getBookingId())
                        && status.equals(p.getStatus()));
    }

    // ─── Seed Data ────────────────────────────────────────────────────────────

    private void seedData() {
        Payment p1 = new Payment();
        p1.setPaymentId("PY-AA1122");
        p1.setBookingId("BK-A1B2C3");
        p1.setAmount(689.70);
        p1.setMethod("CARD");
        p1.setStatus("COMPLETED");
        p1.setTransactionDate("2026-05-01");
        store.put(p1.getPaymentId(), p1);

        Payment p2 = new Payment();
        p2.setPaymentId("PY-BB3344");
        p2.setBookingId("BK-D4E5F6");
        p2.setAmount(250.80);
        p2.setMethod("BANK_TRANSFER");
        p2.setStatus("COMPLETED");
        p2.setTransactionDate("2026-03-30");
        store.put(p2.getPaymentId(), p2);
    }

}
