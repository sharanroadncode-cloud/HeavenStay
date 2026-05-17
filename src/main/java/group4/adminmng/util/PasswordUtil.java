package group4.adminmng.util;

// ================================================================
// File    : PasswordUtil.java
// Package : com.heavenstay.util
// Desc    : SHA-256 password hashing — no external libraries.
// ================================================================

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordUtil {

    private PasswordUtil() {}

    public static String hash(String plainText) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(plainText.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 unavailable", e);
        }
    }

    public static boolean verify(String plainText, String storedHash) {
        return hash(plainText).equals(storedHash);
    }
}
