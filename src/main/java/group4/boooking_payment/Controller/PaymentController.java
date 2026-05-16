package group4.boooking_payment.Controller;

import group4.boooking_payment.model.Payment;
import group4.boooking_payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/Payment")
@CrossOrigin(origins = "*")   // allows the HTML frontend to call this API


public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    // POST /api/payments  →  process a payment
    @PostMapping
    public ResponseEntity<?> processPayment(@RequestBody Payment payment) {
        try {
            Payment processed = paymentService.processPayment(payment);
            return ResponseEntity.status(HttpStatus.CREATED).body(processed);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // GET /api/payments  →  list all payments
    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    // GET /api/payments/{id}  →  get one payment
    @GetMapping("/{id}")
    public ResponseEntity<?> getPaymentById(@PathVariable String id) {
        return paymentService.getPaymentById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/payments/booking/{bookingId}  →  find payment by booking
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<?> getPaymentByBookingId(@PathVariable String bookingId) {
        return paymentService.getPaymentByBookingId(bookingId)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT /api/payments/{id}/refund  →  refund a payment
    @PutMapping("/{id}/refund")
    public ResponseEntity<?> refundPayment(@PathVariable String id) {
        try {
            return ResponseEntity.ok(paymentService.refundPayment(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // GET /api/payments/{id}/invoice  →  get invoice number
    @GetMapping("/{id}/invoice")
    public ResponseEntity<?> getInvoice(@PathVariable String id) {
        try {
            String invoiceNumber = paymentService.generateInvoice(id);
            return ResponseEntity.ok(Map.of("invoiceNumber", invoiceNumber));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // DELETE /api/payments/{id}  →  delete a payment
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePayment(@PathVariable String id) {
        try {
            paymentService.deletePayment(id);
            return ResponseEntity.ok(Map.of("message", "Payment " + id + " deleted."));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

}
