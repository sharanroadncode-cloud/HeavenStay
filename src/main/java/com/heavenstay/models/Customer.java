package com.heavenstay.models;

public class Customer extends User {
    private String phoneNumber;
    private int loyaltyPoints;

    public Customer(String userId, String name, String email,
                    String password, String phoneNumber, int loyaltyPoints) {
        super(userId, name, email, password, "CUSTOMER");
        this.phoneNumber = phoneNumber;
        this.loyaltyPoints = loyaltyPoints;
    }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public int getLoyaltyPoints() { return loyaltyPoints; }
    public void setLoyaltyPoints(int loyaltyPoints) { this.loyaltyPoints = loyaltyPoints; }
    public void addLoyaltyPoints(int points) { this.loyaltyPoints += points; }

    @Override
    public String getDisplayInfo() {
        return "[CUSTOMER] " + getName() + " | Phone: " + phoneNumber + " | Points: " + loyaltyPoints;
    }

    @Override
    public String toFileString() {
        return super.toFileString() + "|" + phoneNumber + "|" + loyaltyPoints;
    }

    public static Customer fromFileString(String line) {
        String[] p = line.split("\\|");
        if (p.length < 7) throw new IllegalArgumentException("Invalid customer line: " + line);
        return new Customer(p[0], p[1], p[2], p[3], p[5], Integer.parseInt(p[6]));
    }
}