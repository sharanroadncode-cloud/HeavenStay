package com.srh.model;

public class CheckIn {

    private String checkInId;
    private String bookingId;
    private String guestId;
    private String staffId;
    private String roomAssigned;
    private String actualArrival;
    private String keyCardId;

    public CheckIn() {}

    public CheckIn(String checkInId, String bookingId, String guestId,
                   String staffId, String roomAssigned, String actualArrival, String keyCardId) {
        this.checkInId = checkInId;
        this.bookingId = bookingId;
        this.guestId = guestId;
        this.staffId = staffId;
        this.roomAssigned = roomAssigned;
        this.actualArrival = actualArrival;
        this.keyCardId = keyCardId;
    }

    public void execute() {
        System.out.println("Check-In: " + checkInId + " | Room: " + roomAssigned + " | Guest: " + guestId);
    }

    @Override
    public String toString() {
        return String.join("|", checkInId, bookingId, guestId, staffId, roomAssigned, actualArrival, keyCardId);
    }

    public static CheckIn fromString(String line) {
        String[] p = line.split("\\|");
        if (p.length != 7) throw new IllegalArgumentException("Invalid CheckIn record: " + line);
        return new CheckIn(p[0].trim(), p[1].trim(), p[2].trim(), p[3].trim(), p[4].trim(), p[5].trim(), p[6].trim());
    }

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