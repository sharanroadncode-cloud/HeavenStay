package com.heavenstay.filehandler;

import com.heavenstay.models.Admin;
import com.heavenstay.models.Customer;
import com.heavenstay.models.Receptionist;
import com.heavenstay.models.User;

import java.util.ArrayList;
import java.util.List;

public class UserFileHandler {

    private static final String FILE_PATH = "data/users.txt";

    public static List<User> loadAllUsers() {
        List<String> lines = FileManager.readLines(FILE_PATH);
        List<User> users = new ArrayList<>();
        for (String line : lines) {
            try {
                String[] parts = line.split("\\|");
                if (parts.length < 5) continue;
                switch (parts[4]) {
                    case "ADMIN":
                        users.add(Admin.fromFileString(line));
                        break;
                    case "RECEPTIONIST":
                        users.add(Receptionist.fromFileString(line));
                        break;
                    case "CUSTOMER":
                        users.add(Customer.fromFileString(line));
                        break;
                    default:
                        System.out.println("[WARN] Unknown role: " + parts[4]);
                }
            } catch (Exception e) {
                System.out.println("[WARN] Skipping user line: " + line);
            }
        }
        return users;
    }

    public static boolean saveAllUsers(List<User> users) {
        List<String> lines = new ArrayList<>();
        for (User u : users) lines.add(u.toFileString());
        return FileManager.writeLines(FILE_PATH, lines);
    }

    public static boolean appendUser(User user) {
        return FileManager.appendLine(FILE_PATH, user.toFileString());
    }

    public static User findById(String userId) {
        if (userId == null || userId.trim().isEmpty()) return null;
        for (User u : loadAllUsers()) {
            if (u.getUserId().equals(userId)) return u;
        }
        return null;
    }

    public static User findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) return null;
        for (User u : loadAllUsers()) {
            if (u.getEmail().equalsIgnoreCase(email)) return u;
        }
        return null;
    }

    public static boolean emailExists(String email) {
        return findByEmail(email) != null;
    }

    public static boolean updateUser(User updated) {
        if (updated == null) return false;
        List<User> users = loadAllUsers();
        boolean found = false;
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUserId().equals(updated.getUserId())) {
                users.set(i, updated);
                found = true;
                break;
            }
        }
        if (!found) return false;
        return saveAllUsers(users);
    }

    public static boolean deleteUser(String userId) {
        if (userId == null || userId.trim().isEmpty()) return false;
        List<User> users = loadAllUsers();
        boolean removed = users.removeIf(u -> u.getUserId().equals(userId));
        if (!removed) return false;
        return saveAllUsers(users);
    }
}