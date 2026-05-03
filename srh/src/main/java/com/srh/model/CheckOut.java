package com.srh.model;

public class CheckOut {

    private String checkOutId;
    private String bookingId;
    private String guestId;
    private String staffId;
    private String actualDeparture;
    private double outstandingCharges;
    private double finalTotal;

    public CheckOut() {}

    public CheckOut(String checkOutId, String bookingId, String guestId,
                    String staffId, String actualDeparture, double outstandingCharges, double finalTotal) {
        this.checkOutId = checkOutId;
        this.bookingId = bookingId;
        this.guestId = guestId;
        this.staffId = staffId;
        this.actualDeparture = actualDeparture;
        this.outstandingCharges = outstandingCharges;
        this.finalTotal = finalTotal;
    }

    public void execute() {
        System.out.println("Check-Out: " + checkOutId + " | Guest: " + guestId + " | Total: " + finalTotal);
    }

    @Override
    public String toString() {
        return String.join("|", checkOutId, bookingId, guestId, staffId,
                actualDeparture, String.valueOf(outstandingCharges), String.valueOf(finalTotal));
    }

    public static CheckOut fromString(String line) {
        String[] p = line.split("\\|");
        if (p.length != 7) throw new IllegalArgumentException("Invalid CheckOut record: " + line);
        return new CheckOut(p[0].trim(), p[1].trim(), p[2].trim(), p[3].trim(),
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

    public String getActualDeparture() { return actualDeparture; }
    public void setActualDeparture(String actualDeparture) { this.actualDeparture = actualDeparture; }

    public double getOutstandingCharges() { return outstandingCharges; }
    public void setOutstandingCharges(double outstandingCharges) { this.outstandingCharges = outstandingCharges; }

    public double getFinalTotal() { return finalTotal; }
    public void setFinalTotal(double finalTotal) { this.finalTotal = finalTotal; }
}