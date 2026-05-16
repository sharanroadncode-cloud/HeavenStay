package com.heaven.heavenstay.controller;

import com.heaven.heavenstay.model.GuestCheckIn;
import com.heaven.heavenstay.service.CheckInService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/checkin")
@CrossOrigin(origins = "*")
public class CheckInController {

    private final CheckInService checkInService;

    public CheckInController(CheckInService checkInService) {
        this.checkInService = checkInService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestBody Map<String, String> body) {
        Map<String, Object> res = new HashMap<>();
        try {
            String bookingId = body.get("bookingId");
            String guestId = body.get("guestId");
            String staffId = body.get("staffId");
            String roomNumber = body.get("roomNumber");
            if (isBlank(bookingId) || isBlank(guestId) || isBlank(staffId) || isBlank(roomNumber)) {
                res.put("success", false);
                res.put("message", "All fields required: bookingId, guestId, staffId, roomNumber");
                return ResponseEntity.badRequest().body(res);
            }
            GuestCheckIn checkIn = checkInService.checkIn(bookingId, guestId, staffId, roomNumber);
            res.put("success", true);
            res.put("message", "Guest checked in successfully");
            res.put("data", checkIn);
            return ResponseEntity.status(HttpStatus.CREATED).body(res);
        } catch (Exception e) {
            res.put("success", false);
            res.put("message", "Check-in failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll() {
        Map<String, Object> res = new HashMap<>();
        try {
            List<GuestCheckIn> list = checkInService.getAllCheckIns();
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

    private boolean isBlank(String v) {
        return v == null || v.trim().isEmpty();
    }
}