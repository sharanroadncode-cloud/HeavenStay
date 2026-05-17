package group4.adminmng.service;

// ================================================================
// File    : AdminService.java
// Package : com.heavenstay.service
// Desc    : Business logic for Admin Management (Component 05).
//           Covers: admin CRUD, permissions, activity logging,
//           report generation, hotel config management.
// ================================================================

import group4.adminmng.model.*;
import group4.adminmng.repository.*;
import group4.adminmng.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", java.util.Locale.ROOT);

    private final AdminRepository      adminRepo;
    private final AdminLogRepository   logRepo;
    private final HotelConfigRepository configRepo;

    @Autowired
    public AdminService(AdminRepository adminRepo,
                        AdminLogRepository logRepo,
                        HotelConfigRepository configRepo) {
        this.adminRepo  = adminRepo;
        this.logRepo    = logRepo;
        this.configRepo = configRepo;
    }

    // ════════════════════════════════════════════════════════
    // AUTH
    // ════════════════════════════════════════════════════════

    /** Returns AdminUser if credentials valid, null otherwise. */
    public AdminUser login(String username, String password) {
        Optional<AdminUser> opt = adminRepo.findByUsername(username);
        if (opt.isEmpty()) return null;
        AdminUser a = opt.orElse(null);
        if (a == null) return null;
        if (!"Active".equals(a.getStatus())) return null;
        if (!PasswordUtil.verify(password, a.getPassword())) return null;
        // Update lastLogin
        a.setLastLogin(LocalDateTime.now().format(FMT));
        adminRepo.update(a);
        logActivity(a.getUserId(), a.getUsername(), "LOGIN");
        return a;
    }

    // ════════════════════════════════════════════════════════
    // ADMIN CRUD (AdminManager methods)
    // ════════════════════════════════════════════════════════

    public List<AdminUser> getAllAdmins() { return adminRepo.findAll(); }

    public Optional<AdminUser> getAdminByUserId(String userId) {
        return adminRepo.findByUserId(userId);
    }

    public Optional<AdminUser> getAdminByAdminId(String adminId) {
        return adminRepo.findByAdminId(adminId);
    }

    /** Register a new admin account. */
    public AdminUser addAdmin(String username, String password, String confirmPassword,
                              String email, String contactNo,
                              String department, String shift,
                              String permissionsRaw) {
        if (adminRepo.existsByUsername(username))
            throw new IllegalArgumentException("Username already taken: " + username);
        if (adminRepo.existsByEmail(email))
            throw new IllegalArgumentException("Email already registered: " + email);
        if (!password.equals(confirmPassword))
            throw new IllegalArgumentException("Passwords do not match.");
        if (password.length() < 6)
            throw new IllegalArgumentException("Password must be at least 6 characters.");

        String adminId = adminRepo.generateAdminId();
        String userId  = adminRepo.generateUserId();
        List<String> perms = parsePermissions(permissionsRaw);

        AdminUser a = new AdminUser(adminId, userId, username,
                PasswordUtil.hash(password), email,
                contactNo, department,
                (shift != null && !shift.isBlank()) ? shift : "Morning",
                perms, "", "Active");
        adminRepo.save(a);
        logActivity(userId, username, "ADMIN_REGISTERED");
        return a;
    }

    /** Update admin account details. */
    public AdminUser updateAdmin(String adminId, String email, String contactNo,
                                 String department, String shift,
                                 String newPassword, String confirmPassword,
                                 String permissionsRaw) {
        AdminUser a = adminRepo.findByAdminId(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found: " + adminId));

        if (!a.getEmail().equalsIgnoreCase(email) && adminRepo.existsByEmail(email))
            throw new IllegalArgumentException("Email already in use.");

        a.setEmail(email);
        a.setContactNo(contactNo);
        a.setDepartment(department);
        a.setShift(shift);
        a.setPermissions(parsePermissions(permissionsRaw));

        if (newPassword != null && !newPassword.isBlank()) {
            if (!newPassword.equals(confirmPassword))
                throw new IllegalArgumentException("Passwords do not match.");
            a.setPassword(PasswordUtil.hash(newPassword));
        }

        adminRepo.update(a);
        logActivity(a.getUserId(), a.getUsername(), "ADMIN_UPDATED");
        return a;
    }

    /** Suspend an admin account. */
    public void suspendAdmin(String adminId) {
        setAdminStatus(adminId, "Suspended", "ADMIN_SUSPENDED");
    }

    /** Restore a suspended admin account. */
    public void restoreAdmin(String adminId) {
        setAdminStatus(adminId, "Active", "ADMIN_RESTORED");
    }

    private void setAdminStatus(String adminId, String status, String action) {
        AdminUser a = adminRepo.findByAdminId(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found."));
        a.setStatus(status);
        adminRepo.update(a);
        logActivity(a.getUserId(), a.getUsername(), action);
    }

    /** Permanently delete an admin account. */
    public void deleteAdmin(String adminId) {
        AdminUser a = adminRepo.findByAdminId(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found."));
        adminRepo.deleteByAdminId(adminId);
        logActivity(a.getUserId(), a.getUsername(), "ADMIN_DELETED");
    }

    /** Update only the permissions list for an admin. */
    public void updateAdminPermissions(String adminId, String permissionsRaw) {
        AdminUser a = adminRepo.findByAdminId(adminId)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found."));
        a.setPermissions(parsePermissions(permissionsRaw));
        adminRepo.update(a);
        logActivity(a.getUserId(), a.getUsername(), "PERMISSIONS_UPDATED");
    }

    public boolean hasPermission(String adminId, String permission) {
        return adminRepo.findByAdminId(adminId)
                .map(a -> a.hasPermission(permission))
                .orElse(false);
    }

    // ════════════════════════════════════════════════════════
    // ACTIVITY LOGGING
    // ════════════════════════════════════════════════════════

    public void logActivity(String userId, String username, String action) {
        String logId  = logRepo.generateLogId();
        String ts     = LocalDateTime.now().format(FMT);
        logRepo.append(new AdminLog(logId, userId, username, action, ts));
    }

    public List<AdminLog> getAdminLogs(String userId) {
        return (userId != null && !userId.isBlank())
                ? logRepo.findByUserId(userId)
                : logRepo.findAll();
    }

    // ════════════════════════════════════════════════════════
    // REPORTS
    // ════════════════════════════════════════════════════════

    public OccupancyReport generateOccupancyReport(String generatedBy, String dateRange) {
        String id = "RPT-OCC-" + System.currentTimeMillis();
        OccupancyReport r = new OccupancyReport(id, generatedBy, dateRange);
        r.generate();   // calls gatherData() + formatOutput()
        return r;
    }

    public RevenueReport generateRevenueReport(String generatedBy, String dateRange) {
        String id = "RPT-REV-" + System.currentTimeMillis();
        RevenueReport r = new RevenueReport(id, generatedBy, dateRange);
        r.generate();
        return r;
    }

    // ════════════════════════════════════════════════════════
    // HOTEL CONFIG
    // ════════════════════════════════════════════════════════

    public HotelConfig getConfig()              { return configRepo.load(); }

    public void saveConfig(HotelConfig config)  { configRepo.save(config); }

    // ════════════════════════════════════════════════════════
    // DASHBOARD STATS
    // ════════════════════════════════════════════════════════

    public long countActiveAdmins() {
        return adminRepo.findAll().stream()
                .filter(a -> "Active".equals(a.getStatus())).count();
    }

    // ── Helpers ──────────────────────────────────────────────
    private List<String> parsePermissions(String raw) {
        if (raw == null || raw.isBlank()) return List.of();
        return Arrays.stream(raw.split(","))
                .map(String::trim).filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
}

