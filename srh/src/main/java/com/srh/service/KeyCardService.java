package com.srh.service;

import com.srh.file.FileUtil;
import com.srh.model.KeyCard;
import com.srh.util.DateUtil;
import com.srh.util.IdGenerator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class KeyCardService {

    private static final int DEFAULT_VALIDITY_DAYS = 7;
    private static final String FILE_NAME = "keycards.txt";
    private final FileUtil fileUtil;

    public KeyCardService(FileUtil fileUtil) {
        this.fileUtil = fileUtil;
    }

    public String issueCard(String roomId, String guestId) {
        String cardId = IdGenerator.generateKeyCardId();
        KeyCard card = new KeyCard(cardId, roomId, guestId, false, DateUtil.now(), DateUtil.daysFromNow(DEFAULT_VALIDITY_DAYS));
        card.activate();
        fileUtil.writeLine(FILE_NAME, card.toString());
        return cardId;
    }

    public KeyCard add(String cardId, String roomId, String guestId, String issuedDate, String expiryDate) {
        String id = (cardId == null || cardId.isBlank()) ? IdGenerator.generateKeyCardId() : cardId;
        String issued = (issuedDate == null || issuedDate.isBlank()) ? DateUtil.now() : issuedDate;
        String expiry = (expiryDate == null || expiryDate.isBlank()) ? DateUtil.daysFromNow(DEFAULT_VALIDITY_DAYS) : expiryDate;
        KeyCard card = new KeyCard(id, roomId, guestId, true, issued, expiry);
        fileUtil.writeLine(FILE_NAME, card.toString());
        return card;
    }

    public void deactivateForGuest(String guestId) {
        List<KeyCard> all = getAll();
        List<String> updated = new ArrayList<>();
        for (KeyCard card : all) {
            if (card.getGuestId().equals(guestId) && card.isActive()) {
                card.deactivate();
            }
            updated.add(card.toString());
        }
        fileUtil.writeAll(FILE_NAME, updated);
    }

    public List<KeyCard> getAll() {
        List<KeyCard> list = new ArrayList<>();
        for (String line : fileUtil.readAll(FILE_NAME)) {
            try {
                list.add(KeyCard.fromString(line));
            } catch (Exception e) {
                System.err.println("Skipping bad keycard record: " + e.getMessage());
            }
        }
        return list;
    }
}