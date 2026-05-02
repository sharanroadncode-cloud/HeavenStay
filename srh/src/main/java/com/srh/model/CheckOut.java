package com.srh.model;

/**
 * CheckOut – represents a guest check-out event at the hotel reception.
 *
 * OOP Principles applied:
 *  - Encapsulation: all fields are private, accessed via getters/setters.
 *  - Abstraction: execute() hides the internal checkout processing logic.
 *
 * File storage format (pipe-delimited):
 *   checkOutId|bookingId|guestId|staffId|actualDeparture|outstandingCharges|finalTotal
 */
public class CheckOut {

    // -----------------------------------------------------------------------
    // Fields (encapsulated – private)
    // -----------------------------------------------------------------------

    private String checkOutId;          // Unique identifier, e.g. CO1718500000000
    private String bookingId;           // References the original booking
    private String guestId;             // The guest checking out
    private String staffId;             // Reception staff handling check-out
    private String actualDeparture;     // Actual date-time of departure
    private double outstandingCharges;  // Extra charges (room service, mini-bar, etc.)
    private double finalTotal;          // Total bill presented to the guest

    // -----------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------

    /** Default no-arg constructor. */
    public CheckOut() {}

    /**
     * Full constructor – used when creating a new check-out from a request.
     */
    public CheckOut(String checkOutId, String bookingId, String guestId,
                    String staffId, String actualDeparture,
                    double outstandingCharges, double finalTotal) {
        this.checkOutId          = checkOutId;
        this.bookingId           = bookingId;
        this.guestId             = guestId;
        this.staffId             = staffId;
        this.actualDeparture     = actualDeparture;
        this.outstandingCharges  = outstandingCharges;
        this.finalTotal          = finalTotal;
    }

    // -----------------------------------------------------------------------
    // Business method
    // -----------------------------------------------------------------------

    /**
     * execute() – performs the logical steps of a check-out.
     * Prints a departure summary to the console.
     */
    public void execute() {
        System.out.println("=== CHECK-OUT EXECUTED ===");
        System.out.println("  Check-Out ID        : " + checkOutId);
        System.out.println("  Booking ID          : " + bookingId);
        System.out.println("  Guest ID            : " + guestId);
        System.out.println("  Departure Time      : " + actualDeparture);
        System.out.printf( "  Outstanding Charges : LKR %.2f%n", outstandingCharges);
        System.out.printf( "  Final Total         : LKR %.2f%n", finalTotal);
        System.out.println("==========================");
    }

    // -----------------------------------------------------------------------
    // Serialization helpers (file I/O)
    // -----------------------------------------------------------------------

    /**
     * Serialize this object to the pipe-delimited storage format.
     * Format: checkOutId|bookingId|guestId|staffId|actualDeparture|outstandingCharges|finalTotal
     */
    @Override
    public String toString() {
        return String.join("|",
                checkOutId,
                bookingId,
                guestId,
                staffId,
                actualDeparture,
                String.valueOf(outstandingCharges),
                String.valueOf(finalTotal));
    }

    /**
     * Deserialize a pipe-delimited line from checkouts.txt into a CheckOut object.
     *
     * @param line  raw line from file
     * @return      populated CheckOut instance
     * @throws IllegalArgumentException if line has wrong number of fields
     */
    public static CheckOut fromString(String line) {
        String[] parts = line.split("\\|");
        if (parts.length != 7) {
            throw new IllegalArgumentException(
                    "Invalid CheckOut record: expected 7 fields, got "
                    + parts.length + " in line: " + line);
        }
        return new CheckOut(
                parts[0].trim(),                          // checkOutId
                parts[1].trim(),                          // bookingId
                parts[2].trim(),                          // guestId
                parts[3].trim(),                          // staffId
                parts[4].trim(),                          // actualDeparture
                Double.parseDouble(parts[5].trim()),      // outstandingCharges
                Double.parseDouble(parts[6].trim())       // finalTotal
        );
    }

    // -----------------------------------------------------------------------
    // Getters and Setters
    // -----------------------------------------------------------------------

    public String getCheckOutId() { return checkOutId; }
    public void setCheckOutId(String checkOutId) { this.checkOutId = checkOutId; }

    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

    public String getGuestId() { return guestId; }
    public void setGuestId(String guestId) { this.guestId = guestId; }

    public String getStaffId() { return staffId; }
    public void setStaffId(String staffId) { this.staffId = staffId; }

    public String getActualDeparture() { return actualDeparture; }
    public void setActualDeparture(String actualDeparture) { this.actualDeparture = actualDeparture; }

    public double getOutstandingCharges() { return outstandingCharges; }
    public void setOutstandingCharges(double outstandingCharges) { this.outstandingCharges = outstandingCharges; }

    public double getFinalTotal() { return finalTotal; }
    public void setFinalTotal(double finalTotal) { this.finalTotal = finalTotal; }
}
