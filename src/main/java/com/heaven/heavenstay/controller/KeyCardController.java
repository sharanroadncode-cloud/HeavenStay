package com.heaven.heavenstay.controller;

import com.heaven.heavenstay.model.RoomKeyCard;
import com.heaven.heavenstay.service.KeyCardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/keycard")
@CrossOrigin(origins = "*")
public class KeyCardController {

    private final KeyCardService keyCardService;

    public KeyCardController(KeyCardService keyCardService) {
        this.keyCardService = keyCardService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> add(@RequestBody Map<String, String> body) {
        Map<String, Object> res = new HashMap<>();
        try {
            String roomNumber = body.get("roomNumber");
            String guestId = body.get("guestId");
            if (isBlank(roomNumber) || isBlank(guestId)) {
                res.put("success", false);
                res.put("message", "Required: roomNumber, guestId");
                return ResponseEntity.badRequest().body(res);
            }
            RoomKeyCard card = keyCardService.addCard(
                    body.get("cardId"), roomNumber, guestId,
                    body.get("issuedDate"), body.get("expiryDate")
            );
            res.put("success", true);
            res.put("message", "Key card issued successfully");
            res.put("data", card);
            return ResponseEntity.status(HttpStatus.CREATED).body(res);
        } catch (Exception e) {
            res.put("success", false);
            res.put("message", "Failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll() {
        Map<String, Object> res = new HashMap<>();
        try {
            List<RoomKeyCard> list = keyCardService.getAllCards();
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