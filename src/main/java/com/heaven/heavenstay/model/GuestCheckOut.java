package com.heaven.heavenstay.model;

public class GuestCheckOut {

    private String checkOutId;
    private String bookingId;
    private String guestId;
    private String staffId;
    private String departureTime;
    private double extraCharges;
    private double totalBill;

    public GuestCheckOut() {}

    public GuestCheckOut(String checkOutId, String bookingId, String guestId,
                         String staffId, String departureTime, double extraCharges, double totalBill) {
        this.checkOutId = checkOutId;
        this.bookingId = bookingId;
        this.guestId = guestId;
        this.staffId = staffId;
        this.departureTime = departureTime;
        this.extraCharges = extraCharges;
        this.totalBill = totalBill;
    }

    public void execute() {
        System.out.println("Check-Out | " + checkOutId + " | Guest: " + guestId + " | Total: LKR " + totalBill);
    }

    @Override
    public String toString() {
        return String.join("|", checkOutId, bookingId, guestId, staffId,
                departureTime, String.valueOf(extraCharges), String.valueOf(totalBill));
    }

    public static GuestCheckOut fromString(String line) {
        String[] p = line.split("\\|");
        if (p.length != 7) throw new IllegalArgumentException("Invalid check-out record: " + line);
        return new GuestCheckOut(p[0].trim(), p[1].trim(), p[2].trim(), p[3].trim(),
                p[4].trim(), Double.parseDouble(p[5].trim()), Double.parseDouble(p[6].trim()));
    }

    public String getCheckOutId() { return checkOutId; }
    public void setCheckOutId(String checkOutId) { this.checkOutId = checkOutId; }

    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

    public String getGuestId() { return guestId; }
    public void setGuestId(String guestId) { this.guestId = guestId; }

    public String getStaffId() { return staffId; }
    public void setStaffId(String staffId) { this.staffId = staffId; }

    public String getDepartureTime() { return departureTime; }
    public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }

    public double getExtraCharges() { return extraCharges; }
    public void setExtraCharges(double extraCharges) { this.extraCharges = extraCharges; }

    public double getTotalBill() { return totalBill; }
    public void setTotalBill(double totalBill) { this.totalBill = totalBill; }
}