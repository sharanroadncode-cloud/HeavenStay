package com.srh.model;

public class KeyCard {

    private String cardId;
    private String roomId;
    private String guestId;
    private boolean isActive;
    private String issuedDate;
    private String expiryDate;

    public KeyCard() {}

    public KeyCard(String cardId, String roomId, String guestId,
                   boolean isActive, String issuedDate, String expiryDate) {
        this.cardId = cardId;
        this.roomId = roomId;
        this.guestId = guestId;
        this.isActive = isActive;
        this.issuedDate = issuedDate;
        this.expiryDate = expiryDate;
    }

    public void activate() {
        this.isActive = true;
        System.out.println("KeyCard " + cardId + " activated for room " + roomId);
    }

    public void deactivate() {
        this.isActive = false;
        System.out.println("KeyCard " + cardId + " deactivated");
    }

    @Override
    public String toString() {
        return String.join("|", cardId, roomId, guestId, String.valueOf(isActive), issuedDate, expiryDate);
    }

    public static KeyCard fromString(String line) {
        String[] p = line.split("\\|");
        if (p.length != 6) throw new IllegalArgumentException("Invalid KeyCard record: " + line);
        return new KeyCard(p[0].trim(), p[1].trim(), p[2].trim(),
                Boolean.parseBoolean(p[3].trim()), p[4].trim(), p[5].trim());
    }

    public String getCardId() { return cardId; }
    public void setCardId(String cardId) { this.cardId = cardId; }

    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }

    public String getGuestId() { return guestId; }
    public void setGuestId(String guestId) { this.guestId = guestId; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public String getIssuedDate() { return issuedDate; }
    public void setIssuedDate(String issuedDate) { this.issuedDate = issuedDate; }

    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }
}