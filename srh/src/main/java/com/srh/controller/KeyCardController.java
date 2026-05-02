package com.srh.controller;

import com.srh.model.KeyCard;
import com.srh.service.KeyCardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * KeyCardController – REST controller for key-card endpoints.
 *
 * Endpoints:
 *   POST /keycard  → issue a new key-card manually
 *   GET  /keycard  → retrieve all key-card records
 */
@RestController
@RequestMapping("/keycard")
@CrossOrigin(origins = "*")
public class KeyCardController {

    private final KeyCardService keyCardService;

    /** Constructor injection. */
    public KeyCardController(KeyCardService keyCardService) {
        this.keyCardService = keyCardService;
    }

    // -----------------------------------------------------------------------
    // POST /keycard
    // -----------------------------------------------------------------------

    /**
     * Issue a new key-card manually.
     *
     * Expected JSON body:
     * {
     *   "cardId":     "KC001",       (optional - auto-generated if blank)
     *   "roomId":     "101",
     *   "guestId":    "G001",
     *   "issuedDate": "2024-06-01",  (optional - auto-set if blank)
     *   "expiryDate": "2024-06-08"   (optional - auto-set if blank)
     * }
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> issueKeyCard(
            @RequestBody Map<String, String> body) {

        Map<String, Object> response = new HashMap<>();

        try {
            String cardId     = body.get("cardId");
            String roomId     = body.get("roomId");
            String guestId    = body.get("guestId");
            String issuedDate = body.get("issuedDate");
            String expiryDate = body.get("expiryDate");

            // roomId and guestId are required
            if (isBlank(roomId) || isBlank(guestId)) {
                response.put("success", false);
                response.put("message", "Required fields: roomId, guestId");
                return ResponseEntity.badRequest().body(response);
            }

            KeyCard keyCard = keyCardService.add(
                    cardId, roomId, guestId, issuedDate, expiryDate);

            response.put("success", true);
            response.put("message", "Key-card issued successfully");
            response.put("data",    keyCard);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Key-card issue failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // -----------------------------------------------------------------------
    // GET /keycard
    // -----------------------------------------------------------------------

    /**
     * Retrieve all key-card records.
     *
     * @return JSON array of key-card objects
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllKeyCards() {
        Map<String, Object> response = new HashMap<>();

        try {
            List<KeyCard> keyCards = keyCardService.getAll();
            response.put("success", true);
            response.put("count",   keyCards.size());
            response.put("data",    keyCards);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to retrieve key-cards: " + e.getMessage());
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
