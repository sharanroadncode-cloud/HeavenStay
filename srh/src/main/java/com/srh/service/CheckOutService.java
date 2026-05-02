package com.srh.service;

import com.srh.file.FileUtil;
import com.srh.model.CheckOut;
import com.srh.util.DateUtil;
import com.srh.util.IdGenerator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * CheckOutService – service layer for all check-out business operations.
 *
 * Responsibilities:
 *  - Validate and enrich incoming check-out data.
 *  - Compute the final bill (base + outstanding charges).
 *  - Deactivate the guest's key-card.
 *  - Persist new check-outs to checkouts.txt via FileUtil.
 *  - Load and return all existing check-out records.
 */
@Service
public class CheckOutService {

    /** Base room rate used when no booking cost is stored (demo value). */
    private static final double BASE_ROOM_RATE = 15000.00; // LKR per night

    /** Name of the flat-file used for check-out storage. */
    private static final String FILE_NAME = "checkouts.txt";

    private final FileUtil fileUtil;
    private final KeyCardService keyCardService;

    /**
     * Constructor injection.
     */
    public CheckOutService(FileUtil fileUtil, KeyCardService keyCardService) {
        this.fileUtil       = fileUtil;
        this.keyCardService = keyCardService;
    }

    // -----------------------------------------------------------------------
    // Business operations
    // -----------------------------------------------------------------------

    /**
     * Process a guest check-out:
     *  1. Generate a unique check-out ID.
     *  2. Record the actual departure time.
     *  3. Compute the final total = base rate + outstanding charges.
     *  4. Deactivate all key-cards linked to this guest.
     *  5. Execute the check-out (logs summary to console).
     *  6. Persist the record to disk.
     *
     * @param bookingId          the booking being closed
     * @param guestId            the guest checking out
     * @param staffId            the staff member processing the check-out
     * @param outstandingCharges extra charges accrued during stay
     * @return                   the fully populated CheckOut object
     */
    public CheckOut add(String bookingId, String guestId,
                        String staffId, double outstandingCharges) {

        // 1. Generate IDs and timestamps
        String checkOutId       = IdGenerator.generateCheckOutId();
        String actualDeparture  = DateUtil.now();

        // 2. Compute final total
        double finalTotal = BASE_ROOM_RATE + outstandingCharges;

        // 3. Build the CheckOut model object
        CheckOut checkOut = new CheckOut(
                checkOutId,
                bookingId,
                guestId,
                staffId,
                actualDeparture,
                outstandingCharges,
                finalTotal
        );

        // 4. Deactivate the guest's key-card(s)
        keyCardService.deactivateForGuest(guestId);

        // 5. Execute (business logic / console output)
        checkOut.execute();

        // 6. Persist to flat file
        fileUtil.writeLine(FILE_NAME, checkOut.toString());

        return checkOut;
    }

    /**
     * Retrieve all check-out records from the flat file.
     *
     * @return list of CheckOut objects (empty list if file has no data)
     */
    public List<CheckOut> getAll() {
        List<String> lines      = fileUtil.readAll(FILE_NAME);
        List<CheckOut> checkOuts = new ArrayList<>();

        for (String line : lines) {
            try {
                checkOuts.add(CheckOut.fromString(line));
            } catch (IllegalArgumentException e) {
                System.err.println("Skipping malformed check-out record: " + e.getMessage());
            }
        }
        return checkOuts;
    }
}
