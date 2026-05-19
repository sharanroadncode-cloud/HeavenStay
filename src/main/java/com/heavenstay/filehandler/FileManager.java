package com.heavenstay.filehandler;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileManager {

    public static List<String> readLines(String filePath) {
        List<String> lines = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) return lines;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            System.out.println("[ERROR] Read failed: " + filePath + " | " + e.getMessage());
        }
        return lines;
    }

    public static boolean writeLines(String filePath, List<String> lines) {
        File file = new File(filePath);
        ensureParentExists(file);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            System.out.println("[ERROR] Write failed: " + filePath + " | " + e.getMessage());
            return false;
        }
    }

    public static boolean appendLine(String filePath, String line) {
        File file = new File(filePath);
        ensureParentExists(file);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(line);
            writer.newLine();
            return true;
        } catch (IOException e) {
            System.out.println("[ERROR] Append failed: " + filePath + " | " + e.getMessage());
            return false;
        }
    }

    public static void createIfNotExists(String filePath) {
        File file = new File(filePath);
        ensureParentExists(file);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.out.println("[ERROR] Create failed: " + filePath);
            }
        }
    }

    public static boolean fileExists(String filePath) {
        return new File(filePath).exists();
    }

    private static void ensureParentExists(File file) {
        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }
    }
}