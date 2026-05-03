package com.srh.controller;

import com.srh.model.KeyCard;
import com.srh.service.KeyCardService;
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
    public ResponseEntity<Map<String, Object>> issueKeyCard(@RequestBody Map<String, String> body) {
        Map<String, Object> response = new HashMap<>();
        try {
            String roomId = body.get("roomId");
            String guestId = body.get("guestId");

            if (isBlank(roomId) || isBlank(guestId)) {
                response.put("success", false);
                response.put("message", "Required fields: roomId, guestId");
                return ResponseEntity.badRequest().body(response);
            }

            KeyCard card = keyCardService.add(body.get("cardId"), roomId, guestId, body.get("issuedDate"), body.get("expiryDate"));
            response.put("success", true);
            response.put("message", "Key-card issued successfully");
            response.put("data", card);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Key-card issue failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllKeyCards() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<KeyCard> list = keyCardService.getAll();
            response.put("success", true);
            response.put("count", list.size());
            response.put("data", list);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to retrieve key-cards: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}