package com.heavenstay.models;

public class Admin extends User {
    private String adminLevel;

    public Admin(String userId, String name, String email, String password, String adminLevel) {
        super(userId, name, email, password, "ADMIN");
        this.adminLevel = adminLevel;
    }

    public String getAdminLevel() { return adminLevel; }
    public void setAdminLevel(String adminLevel) { this.adminLevel = adminLevel; }

    @Override
    public String getDisplayInfo() {
        return "[ADMIN] " + getName() + " (Level: " + adminLevel + ")";
    }

    @Override
    public String toFileString() {
        return super.toFileString() + "|" + adminLevel;
    }

    public static Admin fromFileString(String line) {
        String[] p = line.split("\\|");
        if (p.length < 6) throw new IllegalArgumentException("Invalid admin line: " + line);
        return new Admin(p[0], p[1], p[2], p[3], p[5]);
    }
}