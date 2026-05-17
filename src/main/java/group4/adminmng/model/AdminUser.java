package group4.adminmng.model;

// ================================================================
// File    : AdminUser.java
// Package : com.heavenstay.model
// Desc    : Represents an admin account. Stored in admins.txt.
//           Also writes activity entries to admin_logs.txt.
//
// admins.txt format:
//   adminId|userId|username|email|contactNo|department|shift|permissions|lastLogin|status
// ================================================================

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdminUser {

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", java.util.Locale.ROOT);

    private String       adminId;
    private String       userId;
    private String       username;
    private String       password;      // SHA-256 hash
    private String       email;
    private String       contactNo;
    private String       department;
    private String       shift;         // Morning | Evening | Night
    private List<String> permissions;   // comma-separated tokens
    private String       lastLogin;
    private String       status;        // Active | Suspended

    // ── Constructor ──────────────────────────────────────────
    public AdminUser(String adminId, String userId, String username,
                     String password, String email, String contactNo,
                     String department, String shift,
                     List<String> permissions, String lastLogin, String status) {
        this.adminId     = adminId;
        this.userId      = userId;
        this.username    = username;
        this.password    = password;
        this.email       = email;
        this.contactNo   = contactNo;
        this.department  = department;
        this.shift       = shift;
        this.permissions = permissions != null ? permissions : new ArrayList<>();
        this.lastLogin   = lastLogin;
        this.status      = status;
    }

    public AdminUser() { this.permissions = new ArrayList<>(); }

    // ── Permission helpers ───────────────────────────────────
    public boolean hasPermission(String permission) {
        return permissions != null &&
                permissions.stream().anyMatch(p -> p.equalsIgnoreCase(permission));
    }

    public String getPermissionsDisplay() {
        return permissions.isEmpty() ? "None" : String.join(", ", permissions);
    }

    // ── Serialisation ────────────────────────────────────────
    /**
     * Format: adminId|userId|username|password|email|contactNo|department|shift|permissions|lastLogin|status
     */
    @Override
    public String toString() {
        String perms = permissions.isEmpty() ? "-" : String.join(",", permissions);
        String ll    = (lastLogin == null || lastLogin.isBlank()) ? "-" : lastLogin;
        return adminId + "|" + userId + "|" + username + "|" + password + "|"
                + email + "|" + (contactNo != null ? contactNo : "-") + "|"
                + department + "|" + shift + "|" + perms + "|" + ll + "|" + status;
    }

    public static AdminUser fromString(String line) {
        String[] p = line.split("\\|", -1);
        List<String> perms = new ArrayList<>();
        if (p.length > 8 && !"-".equals(p[8]))
            perms = new ArrayList<>(Arrays.asList(p[8].split(",")));
        String ll     = (p.length > 9  && !"-".equals(p[9]))  ? p[9]  : "";
        String status = (p.length > 10) ? p[10] : "Active";
        return new AdminUser(p[0], p[1], p[2], p[3], p[4],
                p[5].equals("-") ? "" : p[5],
                p[6], p[7], perms, ll, status);
    }

    // ── Getters & Setters ────────────────────────────────────
    public String       getAdminId()                   { return adminId; }
    public void         setAdminId(String v)           { adminId = v; }
    public String       getUserId()                    { return userId; }
    public void         setUserId(String v)            { userId = v; }
    public String       getUsername()                  { return username; }
    public void         setUsername(String v)          { username = v; }
    public String       getPassword()                  { return password; }
    public void         setPassword(String v)          { password = v; }
    public String       getEmail()                     { return email; }
    public void         setEmail(String v)             { email = v; }
    public String       getContactNo()                 { return contactNo; }
    public void         setContactNo(String v)         { contactNo = v; }
    public String       getDepartment()                { return department; }
    public void         setDepartment(String v)        { department = v; }
    public String       getShift()                     { return shift; }
    public void         setShift(String v)             { shift = v; }
    public List<String> getPermissions()               { return permissions; }
    public void         setPermissions(List<String> v) { permissions = v; }
    public String       getLastLogin()                 { return lastLogin; }
    public void         setLastLogin(String v)         { lastLogin = v; }
    public String       getStatus()                    { return status; }
    public void         setStatus(String v)            { status = v; }
}
