package com.srh.service;

import com.srh.file.FileUtil;
import com.srh.model.CheckIn;
import com.srh.util.DateUtil;
import com.srh.util.IdGenerator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * CheckInService – service layer for all check-in business operations.
 *
 * Responsibilities:
 *  - Validate and enrich incoming check-in data.
 *  - Persist new check-ins to checkins.txt via FileUtil.
 *  - Load and return all existing check-in records.
 */
@Service
public class CheckInService {

    /** Name of the flat-file used for check-in storage. */
    private static final String FILE_NAME = "checkins.txt";

    private final FileUtil fileUtil;
    private final KeyCardService keyCardService;

    /**
     * Constructor injection – preferred over field injection.
     */
    public CheckInService(FileUtil fileUtil, KeyCardService keyCardService) {
        this.fileUtil       = fileUtil;
        this.keyCardService = keyCardService;
    }

    // -----------------------------------------------------------------------
    // Business operations
    // -----------------------------------------------------------------------

    /**
     * Process a new guest check-in:
     *  1. Generate a unique check-in ID.
     *  2. Record the actual arrival time.
     *  3. Issue (or auto-generate) a key-card for the room.
     *  4. Execute the check-in (logs to console).
     *  5. Persist the record to disk.
     *
     * @param bookingId    the booking being fulfilled
     * @param guestId      the guest checking in
     * @param staffId      the staff member processing the check-in
     * @param roomAssigned the room number assigned to the guest
     * @return             the fully populated CheckIn object
     */
    public CheckIn add(String bookingId, String guestId,
                       String staffId, String roomAssigned) {

        // 1. Generate IDs and timestamps
        String checkInId    = IdGenerator.generateCheckInId();
        String actualArrival = DateUtil.now();

        // 2. Issue a key-card for this room / guest
        String keyCardId = keyCardService.issueCard(roomAssigned, guestId);

        // 3. Build the CheckIn model object
        CheckIn checkIn = new CheckIn(
                checkInId,
                bookingId,
                guestId,
                staffId,
                roomAssigned,
                actualArrival,
                keyCardId
        );

        // 4. Execute (business logic / console output)
        checkIn.execute();

        // 5. Persist to flat file
        fileUtil.writeLine(FILE_NAME, checkIn.toString());

        return checkIn;
    }

    /**
     * Retrieve all check-in records from the flat file.
     *
     * @return list of CheckIn objects (empty list if file has no data)
     */
    public List<CheckIn> getAll() {
        List<String> lines     = fileUtil.readAll(FILE_NAME);
        List<CheckIn> checkIns = new ArrayList<>();

        for (String line : lines) {
            try {
                checkIns.add(CheckIn.fromString(line));
            } catch (IllegalArgumentException e) {
                // Log the bad line and skip it – don't crash the whole request
                System.err.println("Skipping malformed check-in record: " + e.getMessage());
            }
        }
        return checkIns;
    }
}
