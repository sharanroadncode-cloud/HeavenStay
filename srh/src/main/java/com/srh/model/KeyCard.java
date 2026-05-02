package com.srh.model;

/**
 * KeyCard – represents an electronic key-card issued to a guest at check-in.
 *
 * OOP Principles applied:
 *  - Encapsulation: all fields are private, accessed via getters/setters.
 *  - Abstraction: activate() and deactivate() hide the internal state changes.
 *
 * File storage format (pipe-delimited):
 *   cardId|roomId|guestId|isActive|issuedDate|expiryDate
 */
public class KeyCard {

    // -----------------------------------------------------------------------
    // Fields (encapsulated – private)
    // -----------------------------------------------------------------------

    private String  cardId;      // Unique key-card identifier, e.g. KC1718500000000
    private String  roomId;      // Room this card grants access to
    private String  guestId;     // Guest the card is assigned to
    private boolean isActive;    // Whether the card is currently valid
    private String  issuedDate;  // Date-time the card was issued
    private String  expiryDate;  // Date-time the card expires (check-out date)

    // -----------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------

    /** Default no-arg constructor. */
    public KeyCard() {}

    /**
     * Full constructor – used when issuing a new key-card.
     */
    public KeyCard(String cardId, String roomId, String guestId,
                   boolean isActive, String issuedDate, String expiryDate) {
        this.cardId     = cardId;
        this.roomId     = roomId;
        this.guestId    = guestId;
        this.isActive   = isActive;
        this.issuedDate = issuedDate;
        this.expiryDate = expiryDate;
    }

    // -----------------------------------------------------------------------
    // Business methods
    // -----------------------------------------------------------------------

    /**
     * Activate the key-card (e.g. on guest check-in).
     */
    public void activate() {
        this.isActive = true;
        System.out.println("KeyCard [" + cardId + "] activated for room " + roomId);
    }

    /**
     * Deactivate the key-card (e.g. on guest check-out or lost card).
     */
    public void deactivate() {
        this.isActive = false;
        System.out.println("KeyCard [" + cardId + "] deactivated.");
    }

    // -----------------------------------------------------------------------
    // Serialization helpers (file I/O)
    // -----------------------------------------------------------------------

    /**
     * Serialize this object to the pipe-delimited storage format.
     * Format: cardId|roomId|guestId|isActive|issuedDate|expiryDate
     */
    @Override
    public String toString() {
        return String.join("|",
                cardId,
                roomId,
                guestId,
                String.valueOf(isActive),
                issuedDate,
                expiryDate);
    }

    /**
     * Deserialize a pipe-delimited line from keycards.txt into a KeyCard object.
     *
     * @param line  raw line from file
     * @return      populated KeyCard instance
     * @throws IllegalArgumentException if line has wrong number of fields
     */
    public static KeyCard fromString(String line) {
        String[] parts = line.split("\\|");
        if (parts.length != 6) {
            throw new IllegalArgumentException(
                    "Invalid KeyCard record: expected 6 fields, got "
                    + parts.length + " in line: " + line);
        }
        return new KeyCard(
                parts[0].trim(),                            // cardId
                parts[1].trim(),                            // roomId
                parts[2].trim(),                            // guestId
                Boolean.parseBoolean(parts[3].trim()),      // isActive
                parts[4].trim(),                            // issuedDate
                parts[5].trim()                             // expiryDate
        );
    }

    // -----------------------------------------------------------------------
    // Getters and Setters
    // -----------------------------------------------------------------------

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
