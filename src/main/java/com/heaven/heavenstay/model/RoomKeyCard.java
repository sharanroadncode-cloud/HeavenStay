package com.heaven.heavenstay.model;

public class RoomKeyCard {

    private String cardId;
    private String roomNumber;
    private String guestId;
    private boolean active;
    private String issuedDate;
    private String expiryDate;

    public RoomKeyCard() {}

    public RoomKeyCard(String cardId, String roomNumber, String guestId,
                       boolean active, String issuedDate, String expiryDate) {
        this.cardId = cardId;
        this.roomNumber = roomNumber;
        this.guestId = guestId;
        this.active = active;
        this.issuedDate = issuedDate;
        this.expiryDate = expiryDate;
    }

    public void activate() {
        this.active = true;
        System.out.println("Key card " + cardId + " activated for room " + roomNumber);
    }

    public void deactivate() {
        this.active = false;
        System.out.println("Key card " + cardId + " deactivated");
    }

    @Override
    public String toString() {
        return String.join("|", cardId, roomNumber, guestId, String.valueOf(active), issuedDate, expiryDate);
    }

    public static RoomKeyCard fromString(String line) {
        String[] p = line.split("\\|");
        if (p.length != 6) throw new IllegalArgumentException("Invalid key card record: " + line);
        return new RoomKeyCard(p[0].trim(), p[1].trim(), p[2].trim(),
                Boolean.parseBoolean(p[3].trim()), p[4].trim(), p[5].trim());
    }

    public String getCardId() { return cardId; }
    public void setCardId(String cardId) { this.cardId = cardId; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public String getGuestId() { return guestId; }
    public void setGuestId(String guestId) { this.guestId = guestId; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public String getIssuedDate() { return issuedDate; }
    public void setIssuedDate(String issuedDate) { this.issuedDate = issuedDate; }

    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }
}