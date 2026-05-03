package com.srh.controller;

import com.srh.model.CheckOut;
import com.srh.service.CheckOutService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/checkout")
@CrossOrigin(origins = "*")
public class CheckOutController {

    private final CheckOutService checkOutService;

    public CheckOutController(CheckOutService checkOutService) {
        this.checkOutService = checkOutService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createCheckOut(@RequestBody Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();
        try {
            String bookingId = getString(body, "bookingId");
            String guestId = getString(body, "guestId");
            String staffId = getString(body, "staffId");
            double charges = getDouble(body, "outstandingCharges");

            if (isBlank(bookingId) || isBlank(guestId) || isBlank(staffId)) {
                response.put("success", false);
                response.put("message", "Required fields: bookingId, guestId, staffId");
                return ResponseEntity.badRequest().body(response);
            }

            CheckOut checkOut = checkOutService.add(bookingId, guestId, staffId, charges);
            response.put("success", true);
            response.put("message", "Check-out successful");
            response.put("data", checkOut);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Check-out failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllCheckOuts() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<CheckOut> list = checkOutService.getAll();
            response.put("success", true);
            response.put("count", list.size());
            response.put("data", list);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to retrieve check-outs: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    private String getString(Map<String, Object> map, String key) {
        Object val = map.get(key);
        return val == null ? null : val.toString();
    }

    private double getDouble(Map<String, Object> map, String key) {
        Object val = map.get(key);
        if (val == null) return 0.0;
        if (val instanceof Number) return ((Number) val).doubleValue();
        try { return Double.parseDouble(val.toString()); } catch (NumberFormatException e) { return 0.0; }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}