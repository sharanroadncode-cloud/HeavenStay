package com.srh.controller;

import com.srh.model.CheckIn;
import com.srh.service.CheckInService;
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
    public ResponseEntity<Map<String, Object>> createCheckIn(@RequestBody Map<String, String> body) {
        Map<String, Object> response = new HashMap<>();
        try {
            String bookingId = body.get("bookingId");
            String guestId = body.get("guestId");
            String staffId = body.get("staffId");
            String roomAssigned = body.get("roomAssigned");

            if (isBlank(bookingId) || isBlank(guestId) || isBlank(staffId) || isBlank(roomAssigned)) {
                response.put("success", false);
                response.put("message", "All fields are required: bookingId, guestId, staffId, roomAssigned");
                return ResponseEntity.badRequest().body(response);
            }

            CheckIn checkIn = checkInService.add(bookingId, guestId, staffId, roomAssigned);
            response.put("success", true);
            response.put("message", "Check-in successful");
            response.put("data", checkIn);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Check-in failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllCheckIns() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<CheckIn> list = checkInService.getAll();
            response.put("success", true);
            response.put("count", list.size());
            response.put("data", list);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to retrieve check-ins: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}