package group4.adminmng.util;

// ================================================================
// File    : FileUtil.java
// Package : com.heavenstay.util
// Desc    : Shared low-level file I/O using ONLY:
//           FileReader, BufferedReader, FileWriter, BufferedWriter.
//           No JDBC, JPA, or any database dependency.
// ================================================================

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

    private static final int MAX_LINES = 100_000;

    private FileUtil() {}

    private static final File BASE_DIR;
    static {
        try {
            BASE_DIR = new File(System.getProperty("user.dir"), "data").getCanonicalFile();
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private static File sanitize(String path) throws IOException {
        File f = new File(BASE_DIR, new File(path).getName()).getCanonicalFile();
        if (!f.getPath().startsWith(BASE_DIR.getPath() + File.separator)
                && !f.equals(BASE_DIR))
            throw new IOException("Access denied: " + path);
        return f;
    }

    /** Read every non-blank line. Returns empty list if file absent. */
    public static List<String> readAllLines(String path) throws IOException {
        List<String> lines = new ArrayList<>();
        File f = sanitize(path);
        if (!f.exists()) return lines;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            int count = 0;
            while ((line = br.readLine()) != null && count < MAX_LINES) {
                if (!line.isBlank()) { lines.add(line); count++; }
            }
        }
        return lines;
    }

    /** Overwrite entire file with the supplied lines (creates file if missing). */
    public static void writeAllLines(String path, List<String> lines) throws IOException {
        File f = sanitize(path);
        f.getParentFile().mkdirs();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(f, false))) {
            for (String line : lines) { bw.write(line); bw.newLine(); }
        }
    }

    /** Append a single line (creates file if missing). */
    public static void appendLine(String path, String line) throws IOException {
        File f = sanitize(path);
        f.getParentFile().mkdirs();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(f, true))) {
            bw.write(line);
            bw.newLine();
        }
    }

    /** Read a single-line file (used for hotel_config.txt). Returns null if absent. */
    public static String readSingleLine(String path) throws IOException {
        List<String> lines = readAllLines(path);
        return lines.isEmpty() ? null : lines.get(0);
    }

    /** Overwrite file with a single line (used for hotel_config.txt). */
    public static void writeSingleLine(String path, String line) throws IOException {
        writeAllLines(path, List.of(line));
    }
}
