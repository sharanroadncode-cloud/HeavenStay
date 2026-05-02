package com.srh.controller;

import com.srh.model.CheckOut;
import com.srh.service.CheckOutService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CheckOutController – REST controller for check-out endpoints.
 *
 * Endpoints:
 *   POST /checkout  → create a new check-out record
 *   GET  /checkout  → retrieve all check-out records
 */
@RestController
@RequestMapping("/checkout")
@CrossOrigin(origins = "*")
public class CheckOutController {

    private final CheckOutService checkOutService;

    /** Constructor injection. */
    public CheckOutController(CheckOutService checkOutService) {
        this.checkOutService = checkOutService;
    }

    // -----------------------------------------------------------------------
    // POST /checkout
    // -----------------------------------------------------------------------

    /**
     * Create a new check-out.
     *
     * Expected JSON body:
     * {
     *   "bookingId":          "BK001",
     *   "guestId":            "G001",
     *   "staffId":            "S001",
     *   "outstandingCharges": 2500.00
     * }
     *
     * @param body  request body as a map of key-value pairs
     * @return      JSON response with success status and check-out details
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createCheckOut(
            @RequestBody Map<String, Object> body) {

        Map<String, Object> response = new HashMap<>();

        try {
            String bookingId = getString(body, "bookingId");
            String guestId   = getString(body, "guestId");
            String staffId   = getString(body, "staffId");

            // Outstanding charges may come in as Integer or Double from JSON
            double outstandingCharges = getDouble(body, "outstandingCharges");

            // Basic validation
            if (isBlank(bookingId) || isBlank(guestId) || isBlank(staffId)) {
                response.put("success", false);
                response.put("message",
                        "Required fields: bookingId, guestId, staffId, outstandingCharges");
                return ResponseEntity.badRequest().body(response);
            }

            CheckOut checkOut = checkOutService.add(
                    bookingId, guestId, staffId, outstandingCharges);

            response.put("success", true);
            response.put("message", "Check-out successful");
            response.put("data",    checkOut);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Check-out failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // -----------------------------------------------------------------------
    // GET /checkout
    // -----------------------------------------------------------------------

    /**
     * Retrieve all check-out records.
     *
     * @return JSON array of check-out objects
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllCheckOuts() {
        Map<String, Object> response = new HashMap<>();

        try {
            List<CheckOut> checkOuts = checkOutService.getAll();
            response.put("success", true);
            response.put("count",   checkOuts.size());
            response.put("data",    checkOuts);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to retrieve check-outs: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private String getString(Map<String, Object> map, String key) {
        Object val = map.get(key);
        return val == null ? null : val.toString();
    }

    private double getDouble(Map<String, Object> map, String key) {
        Object val = map.get(key);
        if (val == null) return 0.0;
        if (val instanceof Number) return ((Number) val).doubleValue();
        try { return Double.parseDouble(val.toString()); }
        catch (NumberFormatException e) { return 0.0; }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
