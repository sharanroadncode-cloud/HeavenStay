package group4.boooking_payment.model;

public class Booking {
    private String bookingId;
    private String guestId;
    private String roomId;
    private String checkInDate;
    private String checkOutDate;
    private String status;           // PENDING | CONFIRMED | CHECKED_IN | CHECKED_OUT | CANCELLED
    private int    numberOfGuests;
    private String specialRequests;
    private double subtotal;
    private double discount;
    private double tax;
    private double total;

    // ─── Constructors ──────────────────────────────────────────────────────────

    public Booking() {}

    // ─── Business Methods ─────────────────────────────────────────────────────

    public int calculateNights() {
        try {
            java.time.LocalDate ci = java.time.LocalDate.parse(checkInDate);
            java.time.LocalDate co = java.time.LocalDate.parse(checkOutDate);
            return (int) java.time.temporal.ChronoUnit.DAYS.between(ci, co);
        } catch (java.time.format.DateTimeParseException e) {
            return 0;
        }
    }

    public void calculateSubtotal() {
        this.subtotal = getRoomRate() * calculateNights();
    }

    public void calculateDiscount() {
        this.discount = this.subtotal * 0.05;   // 5% discount
    }

    public void calculateTax() {
        this.tax = (this.subtotal - this.discount) * 0.10;  // 10% tax
    }

    public void calculateTotal() {
        this.total = (this.subtotal - this.discount) + this.tax;
    }

    public void confirm() {
        this.status = "CONFIRMED";
    }

    public void cancel() {
        this.status = "CANCELLED";
    }

    private double getRoomRate() {
        if (roomId == null) return 0;
        return switch (roomId) {
            case "R001" -> 15000.00;
            case "R002" -> 25000.00;
            case "R003" -> 50000.00;
            default     -> 0.0;
        };
    }

    // ─── Getters & Setters ────────────────────────────────────────────────────

    public String getBookingId()                       { return bookingId; }
    public void   setBookingId(String bookingId)       { this.bookingId = bookingId; }

    public String getGuestId()                         { return guestId; }
    public void   setGuestId(String guestId)           { this.guestId = guestId; }

    public String getRoomId()                          { return roomId; }
    public void   setRoomId(String roomId)             { this.roomId = roomId; }

    public String getCheckInDate()                     { return checkInDate; }
    public void   setCheckInDate(String checkInDate)   { this.checkInDate = checkInDate; }

    public String getCheckOutDate()                    { return checkOutDate; }
    public void   setCheckOutDate(String checkOutDate) { this.checkOutDate = checkOutDate; }

    public String getStatus()                          { return status; }
    public void   setStatus(String status)             { this.status = status; }

    public int    getNumberOfGuests()                        { return numberOfGuests; }
    public void   setNumberOfGuests(int numberOfGuests)      { this.numberOfGuests = numberOfGuests; }

    public String getSpecialRequests()                             { return specialRequests; }
    public void   setSpecialRequests(String specialRequests)       { this.specialRequests = specialRequests; }

    public double getSubtotal()                        { return subtotal; }
    public void   setSubtotal(double subtotal)         { this.subtotal = subtotal; }

    public double getDiscount()                        { return discount; }
    public void   setDiscount(double discount)         { this.discount = discount; }

    public double getTax()                             { return tax; }
    public void   setTax(double tax)                   { this.tax = tax; }

    public double getTotal()                           { return total; }
    public void   setTotal(double total)               { this.total = total; }

}
