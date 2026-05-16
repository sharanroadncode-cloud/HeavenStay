package group4.boooking_payment.model;

public class Payment {

    private String paymentId;
    private String bookingId;
    private double amount;
    private String method;           // CARD | BANK_TRANSFER | CASH
    private String status;           // PENDING | COMPLETED | FAILED | REFUNDED
    private String transactionDate;

    // ─── Constructors ──────────────────────────────────────────────────────────

    public Payment() {}

    // ─── Business Methods ─────────────────────────────────────────────────────

    public void processPayment() {
        this.status = "COMPLETED";
    }

    public void refund() {
        this.status = "REFUNDED";
    }

    public String generateInvoice() {
        return "INV-" + paymentId.replace("PY-", "");
    }

    // ─── Getters & Setters ────────────────────────────────────────────────────

    public String getPaymentId()                       { return paymentId; }
    public void   setPaymentId(String paymentId)       { this.paymentId = paymentId; }

    public String getBookingId()                       { return bookingId; }
    public void   setBookingId(String bookingId)       { this.bookingId = bookingId; }

    public double getAmount()                          { return amount; }
    public void   setAmount(double amount)             { this.amount = amount; }

    public String getMethod()                          { return method; }
    public void   setMethod(String method)             { this.method = method; }

    public String getStatus()                          { return status; }
    public void   setStatus(String status)             { this.status = status; }

    public String getTransactionDate()                             { return transactionDate; }
    public void   setTransactionDate(String transactionDate)       { this.transactionDate = transactionDate; }

}
