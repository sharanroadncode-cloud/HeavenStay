package group4.adminmng.model;

// ================================================================
// File    : Report.java
// Package : com.heavenstay.model
// Desc    : Abstract base class for all report types.
//           Subclasses: OccupancyReport, RevenueReport
// ================================================================

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class Report {

    protected static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", java.util.Locale.ROOT);

    private String reportId;
    private String generatedBy;    // admin userId
    private String dateRange;      // e.g. "2025-07-01 to 2025-07-31"
    private String generatedDate;

    // ── Constructor ──────────────────────────────────────────
    public Report(String reportId, String generatedBy, String dateRange) {
        this.reportId      = reportId;
        this.generatedBy   = generatedBy;
        this.dateRange     = dateRange;
        this.generatedDate = LocalDateTime.now().format(FMT);
    }

    public Report() {}

    // ── Abstract methods (polymorphism per report type) ──────
    public abstract void   gatherData();
    public abstract String formatOutput();

    // ── Template method ──────────────────────────────────────
    public String generate() {
        gatherData();
        return formatOutput();
    }

    // ── Export to text file ───────────────────────────────────
    public void export(String filename) {
        String content = generate();
        // Restrict exports to the "reports" directory to prevent path traversal
        try {
            File base = new File("reports").getCanonicalFile();
            File f    = new File("reports", new File(filename).getName()).getCanonicalFile();
            if (!f.getPath().startsWith(base.getPath()))
                throw new RuntimeException("Invalid export path: " + filename);
            f.getParentFile().mkdirs();
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(f, false))) {
                bw.write(content);
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot export report: " + e.getMessage(), e);
        }
    }

    // ── Getters & Setters ────────────────────────────────────
    public String getReportId()                  { return reportId; }
    public void   setReportId(String v)          { reportId = v; }
    public String getGeneratedBy()               { return generatedBy; }
    public void   setGeneratedBy(String v)       { generatedBy = v; }
    public String getDateRange()                 { return dateRange; }
    public void   setDateRange(String v)         { dateRange = v; }
    public String getGeneratedDate()             { return generatedDate; }
    public void   setGeneratedDate(String v)     { generatedDate = v; }
}
