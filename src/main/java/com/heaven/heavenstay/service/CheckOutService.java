package com.heaven.heavenstay.service;

import com.heaven.heavenstay.file.FileManager;
import com.heaven.heavenstay.model.GuestCheckOut;
import com.heaven.heavenstay.util.DateHelper;
import com.heaven.heavenstay.util.IdGenerator;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class CheckOutService {

    private static final String FILE = "checkouts.txt";
    private static final double BASE_RATE = 15000.00;
    private final FileManager fileManager;
    private final KeyCardService keyCardService;

    public CheckOutService(FileManager fileManager, KeyCardService keyCardService) {
        this.fileManager = fileManager;
        this.keyCardService = keyCardService;
    }

    public GuestCheckOut checkOut(String bookingId, String guestId, String staffId, double extraCharges) {
        GuestCheckOut checkOut = new GuestCheckOut(
                IdGenerator.checkOutId(), bookingId, guestId, staffId,
                DateHelper.now(), extraCharges, BASE_RATE + extraCharges
        );
        keyCardService.deactivateGuestCards(guestId);
        checkOut.execute();
        fileManager.writeLine(FILE, checkOut.toString());
        return checkOut;
    }

    public List<GuestCheckOut> getAllCheckOuts() {
        List<GuestCheckOut> list = new ArrayList<>();
        for (String line : fileManager.readAll(FILE)) {
            try {
                list.add(GuestCheckOut.fromString(line));
            } catch (Exception e) {
                System.err.println("Skipping bad check-out: " + e.getMessage());
            }
        }
        return list;
    }
}