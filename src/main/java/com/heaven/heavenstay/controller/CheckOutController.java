package com.heaven.heavenstay.controller;

import com.heaven.heavenstay.model.GuestCheckOut;
import com.heaven.heavenstay.service.CheckOutService;
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
    public ResponseEntity<Map<String, Object>> create(@RequestBody Map<String, Object> body) {
        Map<String, Object> res = new HashMap<>();
        try {
            String bookingId = getString(body, "bookingId");
            String guestId = getString(body, "guestId");
            String staffId = getString(body, "staffId");
            double extraCharges = getDouble(body, "extraCharges");
            if (isBlank(bookingId) || isBlank(guestId) || isBlank(staffId)) {
                res.put("success", false);
                res.put("message", "All fields required: bookingId, guestId, staffId");
                return ResponseEntity.badRequest().body(res);
            }
            GuestCheckOut checkOut = checkOutService.checkOut(bookingId, guestId, staffId, extraCharges);
            res.put("success", true);
            res.put("message", "Guest checked out successfully");
            res.put("data", checkOut);
            return ResponseEntity.status(HttpStatus.CREATED).body(res);
        } catch (Exception e) {
            res.put("success", false);
            res.put("message", "Check-out failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll() {
        Map<String, Object> res = new HashMap<>();
        try {
            List<GuestCheckOut> list = checkOutService.getAllCheckOuts();
            res.put("success", true);
            res.put("count", list.size());
            res.put("data", list);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            res.put("success", false);
            res.put("message", "Failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
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

    private boolean isBlank(String v) {
        return v == null || v.trim().isEmpty();
    }
}