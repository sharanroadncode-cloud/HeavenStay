package com.heaven.heavenstay.service;

import com.heaven.heavenstay.file.FileManager;
import com.heaven.heavenstay.model.RoomKeyCard;
import com.heaven.heavenstay.util.DateHelper;
import com.heaven.heavenstay.util.IdGenerator;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class KeyCardService {

    private static final String FILE = "keycards.txt";
    private final FileManager fileManager;

    public KeyCardService(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    public String issueCard(String roomNumber, String guestId) {
        String cardId = IdGenerator.keyCardId();
        RoomKeyCard card = new RoomKeyCard(cardId, roomNumber, guestId, false,
                DateHelper.now(), DateHelper.daysFromNow(7));
        card.activate();
        fileManager.writeLine(FILE, card.toString());
        return cardId;
    }

    public RoomKeyCard addCard(String cardId, String roomNumber, String guestId,
                               String issuedDate, String expiryDate) {
        String id = (cardId == null || cardId.isBlank()) ? IdGenerator.keyCardId() : cardId;
        String issued = (issuedDate == null || issuedDate.isBlank()) ? DateHelper.now() : issuedDate;
        String expiry = (expiryDate == null || expiryDate.isBlank()) ? DateHelper.daysFromNow(7) : expiryDate;
        RoomKeyCard card = new RoomKeyCard(id, roomNumber, guestId, true, issued, expiry);
        fileManager.writeLine(FILE, card.toString());
        return card;
    }

    public void deactivateGuestCards(String guestId) {
        List<RoomKeyCard> all = getAllCards();
        List<String> updated = new ArrayList<>();
        for (RoomKeyCard card : all) {
            if (card.getGuestId().equals(guestId) && card.isActive()) {
                card.deactivate();
            }
            updated.add(card.toString());
        }
        fileManager.writeAll(FILE, updated);
    }

    public List<RoomKeyCard> getAllCards() {
        List<RoomKeyCard> list = new ArrayList<>();
        for (String line : fileManager.readAll(FILE)) {
            try {
                list.add(RoomKeyCard.fromString(line));
            } catch (Exception e) {
                System.err.println("Skipping bad key card: " + e.getMessage());
            }
        }
        return list;
    }
}