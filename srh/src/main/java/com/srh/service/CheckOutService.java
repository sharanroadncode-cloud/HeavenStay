package com.srh.service;

import com.srh.file.FileUtil;
import com.srh.model.CheckOut;
import com.srh.util.DateUtil;
import com.srh.util.IdGenerator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CheckOutService {

    private static final double BASE_ROOM_RATE = 15000.00;
    private static final String FILE_NAME = "checkouts.txt";
    private final FileUtil fileUtil;
    private final KeyCardService keyCardService;

    public CheckOutService(FileUtil fileUtil, KeyCardService keyCardService) {
        this.fileUtil = fileUtil;
        this.keyCardService = keyCardService;
    }

    public CheckOut add(String bookingId, String guestId, String staffId, double outstandingCharges) {
        CheckOut checkOut = new CheckOut(
                IdGenerator.generateCheckOutId(),
                bookingId, guestId, staffId,
                DateUtil.now(),
                outstandingCharges,
                BASE_ROOM_RATE + outstandingCharges
        );
        keyCardService.deactivateForGuest(guestId);
        checkOut.execute();
        fileUtil.writeLine(FILE_NAME, checkOut.toString());
        return checkOut;
    }

    public List<CheckOut> getAll() {
        List<CheckOut> list = new ArrayList<>();
        for (String line : fileUtil.readAll(FILE_NAME)) {
            try {
                list.add(CheckOut.fromString(line));
            } catch (Exception e) {
                System.err.println("Skipping bad checkout record: " + e.getMessage());
            }
        }
        return list;
    }
}