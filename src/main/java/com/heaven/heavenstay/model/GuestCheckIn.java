package com.heaven.heavenstay.model;

public class GuestCheckIn {

    private String checkInId;
    private String bookingId;
    private String guestId;
    private String staffId;
    private String roomNumber;
    private String arrivalTime;
    private String keyCardId;

    public GuestCheckIn() {}

    public GuestCheckIn(String checkInId, String bookingId, String guestId,
                        String staffId, String roomNumber, String arrivalTime, String keyCardId) {
        this.checkInId = checkInId;
        this.bookingId = bookingId;
        this.guestId = guestId;
        this.staffId = staffId;
        this.roomNumber = roomNumber;
        this.arrivalTime = arrivalTime;
        this.keyCardId = keyCardId;
    }

    public void execute() {
        System.out.println("Check-In | " + checkInId + " | Room: " + roomNumber + " | Guest: " + guestId);
    }

    @Override
    public String toString() {
        return String.join("|", checkInId, bookingId, guestId, staffId, roomNumber, arrivalTime, keyCardId);
    }

    public static GuestCheckIn fromString(String line) {
        String[] p = line.split("\\|");
        if (p.length != 7) throw new IllegalArgumentException("Invalid check-in record: " + line);
        return new GuestCheckIn(p[0].trim(), p[1].trim(), p[2].trim(), p[3].trim(), p[4].trim(), p[5].trim(), p[6].trim());
    }

    public String getCheckInId() { return checkInId; }
    public void setCheckInId(String checkInId) { this.checkInId = checkInId; }

    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

    public String getGuestId() { return guestId; }
    public void setGuestId(String guestId) { this.guestId = guestId; }

    public String getStaffId() { return staffId; }
    public void setStaffId(String staffId) { this.staffId = staffId; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public String getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(String arrivalTime) { this.arrivalTime = arrivalTime; }

    public String getKeyCardId() { return keyCardId; }
    public void setKeyCardId(String keyCardId) { this.keyCardId = keyCardId; }
}