package com.srh.service;

import com.srh.file.FileUtil;
import com.srh.model.KeyCard;
import com.srh.util.DateUtil;
import com.srh.util.IdGenerator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * KeyCardService – service layer for all key-card operations.
 *
 * Responsibilities:
 *  - Issue new key-cards on guest check-in.
 *  - Deactivate key-cards on guest check-out.
 *  - Load and return all key-card records.
 *  - Persist all changes to keycards.txt via FileUtil.
 *
 * Note: KeyCardService must NOT depend on CheckInService or CheckOutService
 * to avoid circular dependency. Those services call KeyCardService, not the other way.
 */
@Service
public class KeyCardService {

    /** Default card validity: 7 days from issue. */
    private static final int DEFAULT_VALIDITY_DAYS = 7;

    /** Name of the flat-file used for key-card storage. */
    private static final String FILE_NAME = "keycards.txt";

    private final FileUtil fileUtil;

    /** Constructor injection. */
    public KeyCardService(FileUtil fileUtil) {
        this.fileUtil = fileUtil;
    }

    // -----------------------------------------------------------------------
    // Business operations
    // -----------------------------------------------------------------------

    /**
     * Issue a new, active key-card for the given room and guest.
     *
     * @param roomId   the room this card unlocks
     * @param guestId  the guest the card belongs to
     * @return         the card ID of the newly issued card
     */
    public String issueCard(String roomId, String guestId) {
        String cardId     = IdGenerator.generateKeyCardId();
        String issuedDate = DateUtil.now();
        String expiryDate = DateUtil.daysFromNow(DEFAULT_VALIDITY_DAYS);

        KeyCard card = new KeyCard(cardId, roomId, guestId, false, issuedDate, expiryDate);
        card.activate(); // sets isActive = true and logs

        // Persist the new card
        fileUtil.writeLine(FILE_NAME, card.toString());

        return cardId;
    }

    /**
     * Add a manually specified key-card record (used by the /keycard POST endpoint).
     *
     * @param cardId      provided card ID (or auto-generate if blank)
     * @param roomId      room to assign
     * @param guestId     guest to assign
     * @param issuedDate  issue date string
     * @param expiryDate  expiry date string
     * @return            the saved KeyCard object
     */
    public KeyCard add(String cardId, String roomId, String guestId,
                       String issuedDate, String expiryDate) {

        // Auto-generate ID if caller did not provide one
        String resolvedCardId = (cardId == null || cardId.isBlank())
                ? IdGenerator.generateKeyCardId()
                : cardId;

        String resolvedIssued = (issuedDate == null || issuedDate.isBlank())
                ? DateUtil.now()
                : issuedDate;

        String resolvedExpiry = (expiryDate == null || expiryDate.isBlank())
                ? DateUtil.daysFromNow(DEFAULT_VALIDITY_DAYS)
                : expiryDate;

        KeyCard card = new KeyCard(resolvedCardId, roomId, guestId,
                                   true, resolvedIssued, resolvedExpiry);
        fileUtil.writeLine(FILE_NAME, card.toString());
        return card;
    }

    /**
     * Deactivate all key-cards currently assigned to the given guest.
     * Rewrites the entire file with updated records.
     *
     * @param guestId  the guest whose cards should be deactivated
     */
    public void deactivateForGuest(String guestId) {
        List<KeyCard> allCards = getAll();
        List<String>  updated  = new ArrayList<>();

        for (KeyCard card : allCards) {
            if (card.getGuestId().equals(guestId) && card.isActive()) {
                card.deactivate(); // sets isActive = false
            }
            updated.add(card.toString());
        }

        fileUtil.writeAll(FILE_NAME, updated);
    }

    /**
     * Retrieve all key-card records from the flat file.
     *
     * @return list of KeyCard objects (empty list if file has no data)
     */
    public List<KeyCard> getAll() {
        List<String>  lines    = fileUtil.readAll(FILE_NAME);
        List<KeyCard> keyCards = new ArrayList<>();

        for (String line : lines) {
            try {
                keyCards.add(KeyCard.fromString(line));
            } catch (IllegalArgumentException e) {
                System.err.println("Skipping malformed key-card record: " + e.getMessage());
            }
        }
        return keyCards;
    }
}
