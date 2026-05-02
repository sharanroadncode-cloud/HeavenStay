package com.srh.controller;

import com.srh.model.CheckIn;
import com.srh.service.CheckInService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CheckInController – REST controller for check-in endpoints.
 *
 * Endpoints:
 *   POST /checkin  → create a new check-in record
 *   GET  /checkin  → retrieve all check-in records
 *
 * CORS is enabled for all origins so the frontend (served from a file:// or
 * different port) can communicate with this server.
 */
@RestController
@RequestMapping("/checkin")
@CrossOrigin(origins = "*")  // Allow requests from any origin (frontend dev)
public class CheckInController {

    private final CheckInService checkInService;

    /** Constructor injection. */
    public CheckInController(CheckInService checkInService) {
        this.checkInService = checkInService;
    }

    // -----------------------------------------------------------------------
    // POST /checkin
    // -----------------------------------------------------------------------

    /**
     * Create a new check-in.
     *
     * Expected JSON body:
     * {
     *   "bookingId":   "BK001",
     *   "guestId":     "G001",
     *   "staffId":     "S001",
     *   "roomAssigned": "101"
     * }
     *
     * @param body  request body as a map of key-value pairs
     * @return      JSON response with success status and check-in details
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createCheckIn(
            @RequestBody Map<String, String> body) {

        Map<String, Object> response = new HashMap<>();

        try {
            // Extract fields from the request body
            String bookingId    = body.get("bookingId");
            String guestId      = body.get("guestId");
            String staffId      = body.get("staffId");
            String roomAssigned = body.get("roomAssigned");

            // Basic validation
            if (isBlank(bookingId) || isBlank(guestId)
                    || isBlank(staffId) || isBlank(roomAssigned)) {
                response.put("success", false);
                response.put("message",
                        "All fields are required: bookingId, guestId, staffId, roomAssigned");
                return ResponseEntity.badRequest().body(response);
            }

            // Delegate to service layer
            CheckIn checkIn = checkInService.add(bookingId, guestId, staffId, roomAssigned);

            response.put("success", true);
            response.put("message", "Check-in successful");
            response.put("data",    checkIn);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Check-in failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // -----------------------------------------------------------------------
    // GET /checkin
    // -----------------------------------------------------------------------

    /**
     * Retrieve all check-in records.
     *
     * @return JSON array of check-in objects
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllCheckIns() {
        Map<String, Object> response = new HashMap<>();

        try {
            List<CheckIn> checkIns = checkInService.getAll();
            response.put("success", true);
            response.put("count",   checkIns.size());
            response.put("data",    checkIns);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to retrieve check-ins: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // -----------------------------------------------------------------------
    // Helper
    // -----------------------------------------------------------------------

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
