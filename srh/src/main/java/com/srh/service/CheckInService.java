package com.srh.service;

import com.srh.file.FileUtil;
import com.srh.model.CheckIn;
import com.srh.util.DateUtil;
import com.srh.util.IdGenerator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CheckInService {

    private static final String FILE_NAME = "checkins.txt";
    private final FileUtil fileUtil;
    private final KeyCardService keyCardService;

    public CheckInService(FileUtil fileUtil, KeyCardService keyCardService) {
        this.fileUtil = fileUtil;
        this.keyCardService = keyCardService;
    }

    public CheckIn add(String bookingId, String guestId, String staffId, String roomAssigned) {
        String keyCardId = keyCardService.issueCard(roomAssigned, guestId);
        CheckIn checkIn = new CheckIn(
                IdGenerator.generateCheckInId(),
                bookingId, guestId, staffId, roomAssigned,
                DateUtil.now(), keyCardId
        );
        checkIn.execute();
        fileUtil.writeLine(FILE_NAME, checkIn.toString());
        return checkIn;
    }

    public List<CheckIn> getAll() {
        List<CheckIn> list = new ArrayList<>();
        for (String line : fileUtil.readAll(FILE_NAME)) {
            try {
                list.add(CheckIn.fromString(line));
            } catch (Exception e) {
                System.err.println("Skipping bad checkin record: " + e.getMessage());
            }
        }
        return list;
    }
}