package group4.adminmng.repository;

// ================================================================
// File    : AdminLogRepository.java
// Package : com.heavenstay.repository
// Desc    : Append-only log storage in data/admin_logs.txt.
//           Reads for display; always appends (never overwrites).
//
// admin_logs.txt format:
//   logId|userId|username|action|timestamp
// ================================================================

import group4.adminmng.model.AdminLog;
import group4.adminmng.util.FileUtil;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.*;

@Repository
public class AdminLogRepository {

    private static final String FILE = "data/admin_logs.txt";

    public List<AdminLog> findAll() {
        List<AdminLog> list = new ArrayList<>();
        try {
            for (String line : FileUtil.readAllLines(FILE))
                if (!line.isBlank()) list.add(AdminLog.fromString(line));
        } catch (IOException e) {
            throw new RuntimeException("Cannot read admin_logs.txt: " + e.getMessage(), e);
        }
        // Return newest first
        Collections.reverse(list);
        return list;
    }

    public List<AdminLog> findByUserId(String userId) {
        return findAll().stream()
                .filter(l -> l.getUserId().equals(userId))
                .toList();
    }

    /** Append a new log entry (never overwrites). */
    public void append(AdminLog log) {
        try { FileUtil.appendLine(FILE, log.toString()); }
        catch (IOException e) { throw new RuntimeException("Cannot append log: " + e.getMessage(), e); }
    }

    public String generateLogId() {
        try {
            List<String> lines = FileUtil.readAllLines(FILE);
            return "LOG" + String.format("%04d", lines.size() + 1);
        } catch (IOException e) {
            return "LOG" + System.currentTimeMillis();
        }
    }
}
