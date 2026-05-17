package group4.adminmng.repository;

// ================================================================
// File    : AdminRepository.java
// Package : com.heavenstay.repository
// Desc    : All CRUD on data/admins.txt using plain file I/O.
//           No JDBC / Hibernate / JPA.
//
// admins.txt format:
//   adminId|userId|username|password|email|contactNo|department|shift|permissions|lastLogin|status
// ================================================================

import group4.adminmng.model.AdminUser;
import group4.adminmng.util.FileUtil;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.*;

@Repository
public class AdminRepository {

    private static final String FILE = "data/admins.txt";

    // ── Read all ─────────────────────────────────────────────
    public List<AdminUser> findAll() {
        List<AdminUser> list = new ArrayList<>();
        try {
            for (String line : FileUtil.readAllLines(FILE))
                if (!line.isBlank()) list.add(AdminUser.fromString(line));
        } catch (IOException e) {
            throw new RuntimeException("Cannot read admins.txt: " + e.getMessage(), e);
        }
        return list;
    }

    // ── Find helpers ─────────────────────────────────────────
    public Optional<AdminUser> findByAdminId(String adminId) {
        return findAll().stream().filter(a -> a.getAdminId().equals(adminId)).findFirst();
    }

    public Optional<AdminUser> findByUserId(String userId) {
        return findAll().stream().filter(a -> a.getUserId().equals(userId)).findFirst();
    }

    public Optional<AdminUser> findByUsername(String username) {
        return findAll().stream()
                .filter(a -> a.getUsername().equalsIgnoreCase(username))
                .findFirst();
    }

    public boolean existsByUsername(String username) {
        return findAll().stream().anyMatch(a -> a.getUsername().equalsIgnoreCase(username));
    }

    public boolean existsByEmail(String email) {
        return findAll().stream().anyMatch(a -> a.getEmail().equalsIgnoreCase(email));
    }

    // ── Save ─────────────────────────────────────────────────
    public void save(AdminUser admin) {
        try { FileUtil.appendLine(FILE, admin.toString()); }
        catch (IOException e) { throw new RuntimeException("Cannot save admin: " + e.getMessage(), e); }
    }

    // ── Update ───────────────────────────────────────────────
    public void update(AdminUser updated) {
        try {
            List<String> lines = FileUtil.readAllLines(FILE);
            List<String> out   = new ArrayList<>();
            for (String line : lines) {
                String id = line.split("\\|")[0]; // adminId at index 0
                out.add(id.equals(updated.getAdminId()) ? updated.toString() : line);
            }
            FileUtil.writeAllLines(FILE, out);
        } catch (IOException e) {
            throw new RuntimeException("Cannot update admin: " + e.getMessage(), e);
        }
    }

    // ── Delete ───────────────────────────────────────────────
    public void deleteByAdminId(String adminId) {
        try {
            List<String> lines = FileUtil.readAllLines(FILE);
            List<String> out   = lines.stream()
                    .filter(l -> !l.split("\\|")[0].equals(adminId))
                    .toList();
            FileUtil.writeAllLines(FILE, out);
        } catch (IOException e) {
            throw new RuntimeException("Cannot delete admin: " + e.getMessage(), e);
        }
    }

    // ── ID generators ────────────────────────────────────────
    public String generateAdminId() {
        Set<String> existing = new HashSet<>();
        findAll().forEach(a -> existing.add(a.getAdminId()));
        for (int i = 1; i <= 9999; i++) {
            String id = "ADM" + String.format("%03d", i);
            if (!existing.contains(id)) return id;
        }
        return "ADM" + System.currentTimeMillis();
    }

    public String generateUserId() {
        Set<String> existing = new HashSet<>();
        findAll().forEach(a -> existing.add(a.getUserId()));
        for (int i = 1; i <= 9999; i++) {
            String id = "U" + String.format("%03d", i);
            if (!existing.contains(id)) return id;
        }
        return "U" + System.currentTimeMillis();
    }
}
