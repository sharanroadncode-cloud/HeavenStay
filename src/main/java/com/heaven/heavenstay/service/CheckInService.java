package com.heaven.heavenstay.service;

import com.heaven.heavenstay.file.FileManager;
import com.heaven.heavenstay.model.GuestCheckIn;
import com.heaven.heavenstay.util.DateHelper;
import com.heaven.heavenstay.util.IdGenerator;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class CheckInService {

    private static final String FILE = "checkins.txt";
    private final FileManager fileManager;
    private final KeyCardService keyCardService;

    public CheckInService(FileManager fileManager, KeyCardService keyCardService) {
        this.fileManager = fileManager;
        this.keyCardService = keyCardService;
    }

    public GuestCheckIn checkIn(String bookingId, String guestId, String staffId, String roomNumber) {
        String keyCardId = keyCardService.issueCard(roomNumber, guestId);
        GuestCheckIn checkIn = new GuestCheckIn(
                IdGenerator.checkInId(), bookingId, guestId, staffId,
                roomNumber, DateHelper.now(), keyCardId
        );
        checkIn.execute();
        fileManager.writeLine(FILE, checkIn.toString());
        return checkIn;
    }

    public List<GuestCheckIn> getAllCheckIns() {
        List<GuestCheckIn> list = new ArrayList<>();
        for (String line : fileManager.readAll(FILE)) {
            try {
                list.add(GuestCheckIn.fromString(line));
            } catch (Exception e) {
                System.err.println("Skipping bad check-in: " + e.getMessage());
            }
        }
        return list;
    }
}