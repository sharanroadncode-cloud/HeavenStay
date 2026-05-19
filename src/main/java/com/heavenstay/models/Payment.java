package com.heavenstay.models;

public class Payment {
    public static final String METHOD_CASH   = "CASH";
    public static final String METHOD_CARD   = "CARD";
    public static final String METHOD_ONLINE = "ONLINE";

    public static final String STATUS_PAID     = "PAID";
    public static final String STATUS_PENDING  = "PENDING";
    public static final String STATUS_REFUNDED = "REFUNDED";

    private String paymentId;
    private String bookingId;
    private String customerId;
    private double amount;
    private String paymentMethod;
    private String status;
    private String paymentDate;

    public Payment(String paymentId, String bookingId, String customerId,
                   double amount, String paymentMethod, String status, String paymentDate) {
        this.paymentId = paymentId;
        this.bookingId = bookingId;
        this.customerId = customerId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.paymentDate = paymentDate;
    }

    public String getPaymentId() { return paymentId; }
    public String getBookingId() { return bookingId; }
    public String getCustomerId() { return customerId; }
    public double getAmount() { return amount; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getStatus() { return status; }
    public String getPaymentDate() { return paymentDate; }

    public void setStatus(String status) { this.status = status; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String toFileString() {
        return paymentId + "|" + bookingId + "|" + customerId + "|"
                + amount + "|" + paymentMethod + "|" + status + "|" + paymentDate;
    }

    public static Payment fromFileString(String line) {
        String[] p = line.split("\\|");
        if (p.length < 7) throw new IllegalArgumentException("Invalid payment line: " + line);
        return new Payment(p[0], p[1], p[2], Double.parseDouble(p[3]), p[4], p[5], p[6]);
    }

    @Override
    public String toString() {
        return "Payment " + paymentId + " | Booking " + bookingId
                + " | LKR " + amount + " | " + paymentMethod + " | " + status;
    }
}