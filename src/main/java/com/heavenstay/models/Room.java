package com.heavenstay.models;

public class Room {
    public static final String TYPE_SINGLE  = "SINGLE";
    public static final String TYPE_DOUBLE  = "DOUBLE";
    public static final String TYPE_SUITE   = "SUITE";
    public static final String TYPE_DELUXE  = "DELUXE";

    public static final String STATUS_AVAILABLE   = "AVAILABLE";
    public static final String STATUS_BOOKED      = "BOOKED";
    public static final String STATUS_CLEANING    = "CLEANING";
    public static final String STATUS_MAINTENANCE = "MAINTENANCE";

    private String roomNumber;
    private String type;
    private double pricePerNight;
    private String status;
    private int floorNumber;
    private String description;

    public Room(String roomNumber, String type, double pricePerNight,
                String status, int floorNumber, String description) {
        this.roomNumber = roomNumber;
        this.type = type;
        this.pricePerNight = pricePerNight;
        this.status = status;
        this.floorNumber = floorNumber;
        this.description = description;
    }

    public String getRoomNumber() { return roomNumber; }
    public String getType() { return type; }
    public double getPricePerNight() { return pricePerNight; }
    public String getStatus() { return status; }
    public int getFloorNumber() { return floorNumber; }
    public String getDescription() { return description; }

    public void setType(String type) { this.type = type; }
    public void setPricePerNight(double pricePerNight) { this.pricePerNight = pricePerNight; }
    public void setStatus(String status) { this.status = status; }
    public void setFloorNumber(int floorNumber) { this.floorNumber = floorNumber; }
    public void setDescription(String description) { this.description = description; }

    public boolean isAvailable() {
        return STATUS_AVAILABLE.equals(this.status);
    }

    public String toFileString() {
        return roomNumber + "|" + type + "|" + pricePerNight + "|"
                + status + "|" + floorNumber + "|" + description;
    }

    public static Room fromFileString(String line) {
        String[] p = line.split("\\|");
        if (p.length < 6) throw new IllegalArgumentException("Invalid room line: " + line);
        return new Room(p[0], p[1], Double.parseDouble(p[2]),
                p[3], Integer.parseInt(p[4]), p[5]);
    }

    @Override
    public String toString() {
        return "Room " + roomNumber + " | " + type
                + " | LKR " + pricePerNight + "/night | " + status;
    }
}