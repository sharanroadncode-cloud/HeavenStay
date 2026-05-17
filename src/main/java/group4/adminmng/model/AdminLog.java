package group4.adminmng.model;

// ================================================================
// File    : AdminLog.java
// Package : com.heavenstay.model
// Desc    : Represents a single admin activity log entry.
//           Stored in admin_logs.txt.
//
// admin_logs.txt format:
//   logId|userId|username|action|timestamp
// ================================================================

public class AdminLog {

    private String logId;
    private String userId;
    private String username;
    private String action;
    private String timestamp;  // yyyy-MM-dd HH:mm:ss

    public AdminLog(String logId, String userId, String username,
                    String action, String timestamp) {
        this.logId     = logId;
        this.userId    = userId;
        this.username  = username;
        this.action    = action;
        this.timestamp = timestamp;
    }

    public AdminLog() {}

    @Override
    public String toString() {
        return logId + "|" + userId + "|" + username + "|" + action + "|" + timestamp;
    }

    public static AdminLog fromString(String line) {
        String[] p = line.split("\\|", -1);
        return new AdminLog(p[0], p[1], p[2], p[3], p.length > 4 ? p[4] : "");
    }

    // Getters & Setters
    public String getLogId()            { return logId; }
    public void   setLogId(String v)    { logId = v; }
    public String getUserId()           { return userId; }
    public void   setUserId(String v)   { userId = v; }
    public String getUsername()         { return username; }
    public void   setUsername(String v) { username = v; }
    public String getAction()           { return action; }
    public void   setAction(String v)   { action = v; }
    public String getTimestamp()        { return timestamp; }
    public void   setTimestamp(String v){ timestamp = v; }
}
