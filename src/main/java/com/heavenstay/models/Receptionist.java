package com.heavenstay.models;

public class Receptionist extends User {
    private String shiftTime;

    public Receptionist(String userId, String name, String email, String password, String shiftTime) {
        super(userId, name, email, password, "RECEPTIONIST");
        this.shiftTime = shiftTime;
    }

    public String getShiftTime() { return shiftTime; }
    public void setShiftTime(String shiftTime) { this.shiftTime = shiftTime; }

    @Override
    public String getDisplayInfo() {
        return "[RECEPTIONIST] " + getName() + " (Shift: " + shiftTime + ")";
    }

    @Override
    public String toFileString() {
        return super.toFileString() + "|" + shiftTime;
    }

    public static Receptionist fromFileString(String line) {
        String[] p = line.split("\\|");
        if (p.length < 6) throw new IllegalArgumentException("Invalid receptionist line: " + line);
        return new Receptionist(p[0], p[1], p[2], p[3], p[5]);
    }
}