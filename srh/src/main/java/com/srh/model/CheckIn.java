package com.srh.model;

/**
 * CheckIn – represents a guest check-in event at the hotel reception.
 *
 * OOP Principles applied:
 *  - Encapsulation: all fields are private, accessed via getters/setters.
 *  - Abstraction: execute() hides the internal logic of what a check-in does.
 *
 * File storage format (pipe-delimited):
 *   checkInId|bookingId|guestId|staffId|roomAssigned|actualArrival|keyCardId
 */
public class CheckIn {

    // -----------------------------------------------------------------------
    // Fields (encapsulated – private)
    // -----------------------------------------------------------------------

    private String checkInId;       // Unique identifier, e.g. CI1718500000000
    private String bookingId;       // References an existing booking
    private String guestId;         // The guest being checked in
    private String staffId;         // Reception staff handling the check-in
    private String roomAssigned;    // Room number given to the guest
    private String actualArrival;   // Actual date-time of arrival
    private String keyCardId;       // Key-card issued to the guest

    // -----------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------

    /** Default no-arg constructor (required by some frameworks). */
    public CheckIn() {}

    /**
     * Full constructor – used when creating a new check-in from a request.
     */
    public CheckIn(String checkInId, String bookingId, String guestId,
                   String staffId, String roomAssigned,
                   String actualArrival, String keyCardId) {
        this.checkInId     = checkInId;
        this.bookingId     = bookingId;
        this.guestId       = guestId;
        this.staffId       = staffId;
        this.roomAssigned  = roomAssigned;
        this.actualArrival = actualArrival;
        this.keyCardId     = keyCardId;
    }

    // -----------------------------------------------------------------------
    // Business method
    // -----------------------------------------------------------------------

    /**
     * execute() – performs the logical steps of a check-in.
     * Prints a confirmation to the console (simulating real processing).
     */
    public void execute() {
        System.out.println("=== CHECK-IN EXECUTED ===");
        System.out.println("  Check-In ID   : " + checkInId);
        System.out.println("  Booking ID    : " + bookingId);
        System.out.println("  Guest ID      : " + guestId);
        System.out.println("  Room Assigned : " + roomAssigned);
        System.out.println("  Arrival Time  : " + actualArrival);
        System.out.println("  Key Card      : " + keyCardId);
        System.out.println("=========================");
    }

    // -----------------------------------------------------------------------
    // Serialization helpers (file I/O)
    // -----------------------------------------------------------------------

    /**
     * Serialize this object to the pipe-delimited storage format.
     * Format: checkInId|bookingId|guestId|staffId|roomAssigned|actualArrival|keyCardId
     */
    @Override
    public String toString() {
        return String.join("|",
                checkInId,
                bookingId,
                guestId,
                staffId,
                roomAssigned,
                actualArrival,
                keyCardId);
    }

    /**
     * Deserialize a pipe-delimited line from checkins.txt into a CheckIn object.
     *
     * @param line  raw line from file, e.g. "CI123|BK1|G1|S1|101|2024-06-01 14:00:00|KC1"
     * @return      populated CheckIn instance
     * @throws IllegalArgumentException if the line has wrong number of fields
     */
    public static CheckIn fromString(String line) {
        String[] parts = line.split("\\|");
        if (parts.length != 7) {
            throw new IllegalArgumentException(
                    "Invalid CheckIn record: expected 7 fields, got "
                    + parts.length + " in line: " + line);
        }
        return new CheckIn(
                parts[0].trim(),  // checkInId
                parts[1].trim(),  // bookingId
                parts[2].trim(),  // guestId
                parts[3].trim(),  // staffId
                parts[4].trim(),  // roomAssigned
                parts[5].trim(),  // actualArrival
                parts[6].trim()   // keyCardId
        );
    }

    // -----------------------------------------------------------------------
    // Getters and Setters
    // -----------------------------------------------------------------------

    public String getCheckInId() { return checkInId; }
    public void setCheckInId(String checkInId) { this.checkInId = checkInId; }

    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

    public String getGuestId() { return guestId; }
    public void setGuestId(String guestId) { this.guestId = guestId; }

    public String getStaffId() { return staffId; }
    public void setStaffId(String staffId) { this.staffId = staffId; }

    public String getRoomAssigned() { return roomAssigned; }
    public void setRoomAssigned(String roomAssigned) { this.roomAssigned = roomAssigned; }

    public String getActualArrival() { return actualArrival; }
    public void setActualArrival(String actualArrival) { this.actualArrival = actualArrival; }

    public String getKeyCardId() { return keyCardId; }
    public void setKeyCardId(String keyCardId) { this.keyCardId = keyCardId; }
}
