package com.heaven.heavenstay.file;

import org.springframework.stereotype.Component;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class FileManager {

    private static final String DATA_DIR = "data";

    public FileManager() {
        new File(DATA_DIR).mkdirs();
    }

    public void writeLine(String fileName, String data) {
        Path path = Paths.get(DATA_DIR, fileName);
        ensureFile(path);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile(), true))) {
            writer.write(data);
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException("Write failed: " + fileName, e);
        }
    }

    public List<String> readAll(String fileName) {
        Path path = Paths.get(DATA_DIR, fileName);
        List<String> lines = new ArrayList<>();
        if (!Files.exists(path)) return lines;
        try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) lines.add(line.trim());
            }
        } catch (IOException e) {
            throw new RuntimeException("Read failed: " + fileName, e);
        }
        return lines;
    }

    public void writeAll(String fileName, List<String> lines) {
        Path path = Paths.get(DATA_DIR, fileName);
        ensureFile(path);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile(), false))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Overwrite failed: " + fileName, e);
        }
    }

    private void ensureFile(Path path) {
        if (!Files.exists(path)) {
            try {
                Files.createFile(path);
            } catch (IOException e) {
                throw new RuntimeException("Could not create file: " + path, e);
            }
        }
    }
}