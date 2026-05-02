package com.srh.util;

/**
 * IdGenerator – utility for generating unique, timestamp-based IDs.
 *
 * Format: PREFIX + current epoch milliseconds
 * Examples:
 *   CI1718000000001  → Check-In ID
 *   CO1718000000002  → Check-Out ID
 *   KC1718000000003  → Key-Card ID
 */
public class IdGenerator {

    // Private constructor – this is a static utility class
    private IdGenerator() {}

    /**
     * Generate a check-in ID.
     * @return String like "CI1718500000000"
     */
    public static String generateCheckInId() {
        return "CI" + System.currentTimeMillis();
    }

    /**
     * Generate a check-out ID.
     * @return String like "CO1718500000000"
     */
    public static String generateCheckOutId() {
        return "CO" + System.currentTimeMillis();
    }

    /**
     * Generate a key-card ID.
     * @return String like "KC1718500000000"
     */
    public static String generateKeyCardId() {
        return "KC" + System.currentTimeMillis();
    }
}
