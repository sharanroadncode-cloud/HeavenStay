package com.srh.file;

import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * FileUtil – central utility for all .txt file I/O operations.
 *
 * Responsibilities:
 *  - Ensure data files and directories exist before use.
 *  - Append a single line to a given file.
 *  - Read all non-empty lines from a given file.
 *
 * All storage files live inside the "data/" directory at the project root.
 */
@Component
public class FileUtil {

    /** Root directory where all .txt storage files are kept. */
    private static final String DATA_DIR = "data";

    // -----------------------------------------------------------------------
    // Constructor – ensure the data directory exists at startup
    // -----------------------------------------------------------------------

    public FileUtil() {
        ensureDirectoryExists(DATA_DIR);
    }

    // -----------------------------------------------------------------------
    // Public API
    // -----------------------------------------------------------------------

    /**
     * Append a single line of data to the specified file.
     * Creates the file if it does not already exist.
     *
     * @param fileName  simple filename, e.g. "checkins.txt"
     * @param data      the text line to append
     */
    public void writeLine(String fileName, String data) {
        Path filePath = resolveFilePath(fileName);
        ensureFileExists(filePath);

        // Use BufferedWriter in append mode (true = append, not overwrite)
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(filePath.toFile(), true))) {
            writer.write(data);
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to write to file: " + filePath, e);
        }
    }

    /**
     * Read every non-empty line from the specified file.
     * Returns an empty list if the file does not yet exist.
     *
     * @param fileName  simple filename, e.g. "checkins.txt"
     * @return          list of raw text lines
     */
    public List<String> readAll(String fileName) {
        Path filePath = resolveFilePath(fileName);

        if (!Files.exists(filePath)) {
            return new ArrayList<>();
        }

        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new FileReader(filePath.toFile()))) {

            String line;
            while ((line = reader.readLine()) != null) {
                // Skip blank lines
                if (!line.trim().isEmpty()) {
                    lines.add(line.trim());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to read from file: " + filePath, e);
        }
        return lines;
    }

    /**
     * Overwrite the entire file with the provided list of lines.
     * Useful for update/delete operations.
     *
     * @param fileName  simple filename
     * @param lines     complete list of lines to write
     */
    public void writeAll(String fileName, List<String> lines) {
        Path filePath = resolveFilePath(fileName);
        ensureFileExists(filePath);

        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(filePath.toFile(), false))) {  // false = overwrite
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to overwrite file: " + filePath, e);
        }
    }

    // -----------------------------------------------------------------------
    // Private helpers
    // -----------------------------------------------------------------------

    /** Build the full path: data/<fileName> */
    private Path resolveFilePath(String fileName) {
        return Paths.get(DATA_DIR, fileName);
    }

    /** Create the data directory if it does not exist. */
    private void ensureDirectoryExists(String dirName) {
        File dir = new File(dirName);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (!created) {
                throw new RuntimeException(
                        "Could not create data directory: " + dirName);
            }
        }
    }

    /** Create the file if it does not exist. */
    private void ensureFileExists(Path filePath) {
        if (!Files.exists(filePath)) {
            try {
                Files.createFile(filePath);
            } catch (IOException e) {
                throw new RuntimeException(
                        "Could not create file: " + filePath, e);
            }
        }
    }
}
