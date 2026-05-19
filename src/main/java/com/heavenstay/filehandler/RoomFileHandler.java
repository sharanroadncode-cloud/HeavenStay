package com.heavenstay.filehandler;

import com.heavenstay.models.Room;

import java.util.ArrayList;
import java.util.List;

public class RoomFileHandler {

    private static final String FILE_PATH = "data/rooms.txt";

    public static List<Room> loadAllRooms() {
        List<String> lines = FileManager.readLines(FILE_PATH);
        List<Room> rooms = new ArrayList<>();
        for (String line : lines) {
            try {
                rooms.add(Room.fromFileString(line));
            } catch (Exception e) {
                System.out.println("[WARN] Skipping room line: " + line);
            }
        }
        return rooms;
    }

    public static boolean saveAllRooms(List<Room> rooms) {
        List<String> lines = new ArrayList<>();
        for (Room r : rooms) lines.add(r.toFileString());
        return FileManager.writeLines(FILE_PATH, lines);
    }

    public static boolean appendRoom(Room room) {
        if (room == null) return false;
        return FileManager.appendLine(FILE_PATH, room.toFileString());
    }

    public static Room findByRoomNumber(String roomNumber) {
        if (roomNumber == null || roomNumber.trim().isEmpty()) return null;
        for (Room r : loadAllRooms()) {
            if (r.getRoomNumber().equals(roomNumber)) return r;
        }
        return null;
    }

    public static List<Room> loadAvailableRooms() {
        List<Room> result = new ArrayList<>();
        for (Room r : loadAllRooms()) {
            if (r.isAvailable()) result.add(r);
        }
        return result;
    }

    public static List<Room> loadRoomsByType(String type) {
        List<Room> result = new ArrayList<>();
        if (type == null || type.trim().isEmpty()) return result;
        for (Room r : loadAllRooms()) {
            if (r.getType().equalsIgnoreCase(type)) result.add(r);
        }
        return result;
    }

    public static boolean updateRoom(Room updated) {
        if (updated == null) return false;
        List<Room> rooms = loadAllRooms();
        boolean found = false;
        for (int i = 0; i < rooms.size(); i++) {
            if (rooms.get(i).getRoomNumber().equals(updated.getRoomNumber())) {
                rooms.set(i, updated);
                found = true;
                break;
            }
        }
        if (!found) return false;
        return saveAllRooms(rooms);
    }

    public static boolean deleteRoom(String roomNumber) {
        if (roomNumber == null || roomNumber.trim().isEmpty()) return false;
        List<Room> rooms = loadAllRooms();
        boolean removed = rooms.removeIf(r -> r.getRoomNumber().equals(roomNumber));
        if (!removed) return false;
        return saveAllRooms(rooms);
    }
}