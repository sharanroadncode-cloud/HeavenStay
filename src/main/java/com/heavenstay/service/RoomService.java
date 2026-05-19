package com.heavenstay.service;

import com.heavenstay.filehandler.RoomFileHandler;
import com.heavenstay.models.Room;
import com.heavenstay.utils.ValidationUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoomService {

    public Room addRoom(String roomNumber, String type, double price,
                        int floor, String description) {
        if (!ValidationUtils.isNotEmpty(roomNumber)) return null;
        if (!ValidationUtils.isValidRoomType(type)) return null;
        if (price <= 0) return null;
        if (RoomFileHandler.findByRoomNumber(roomNumber.trim()) != null) return null;
        Room room = new Room(
                roomNumber.trim(),
                type.toUpperCase(),
                price,
                Room.STATUS_AVAILABLE,
                floor,
                ValidationUtils.sanitize(description)
        );
        boolean saved = RoomFileHandler.appendRoom(room);
        return saved ? room : null;
    }

    public List<Room> getAllRooms() {
        return RoomFileHandler.loadAllRooms();
    }

    public List<Room> getAvailableRooms() {
        return RoomFileHandler.loadAvailableRooms();
    }

    public List<Room> getRoomsByType(String type) {
        if (!ValidationUtils.isNotEmpty(type)) return new ArrayList<>();
        return RoomFileHandler.loadRoomsByType(type);
    }

    public List<Room> getAvailableRoomsByType(String type) {
        List<Room> result = new ArrayList<>();
        if (!ValidationUtils.isNotEmpty(type)) return result;
        for (Room r : RoomFileHandler.loadAvailableRooms()) {
            if (r.getType().equalsIgnoreCase(type)) result.add(r);
        }
        return result;
    }

    public List<Room> getRoomsByMaxPrice(double maxPrice) {
        List<Room> result = new ArrayList<>();
        if (maxPrice <= 0) return result;
        for (Room r : RoomFileHandler.loadAllRooms()) {
            if (r.getPricePerNight() <= maxPrice) result.add(r);
        }
        return result;
    }

    public List<Room> getRoomsByFloor(int floor) {
        List<Room> result = new ArrayList<>();
        for (Room r : RoomFileHandler.loadAllRooms()) {
            if (r.getFloorNumber() == floor) result.add(r);
        }
        return result;
    }

    public Room getRoomByNumber(String roomNumber) {
        if (!ValidationUtils.isNotEmpty(roomNumber)) return null;
        return RoomFileHandler.findByRoomNumber(roomNumber.trim());
    }

    public boolean updateRoomPrice(String roomNumber, double newPrice) {
        if (!ValidationUtils.isNotEmpty(roomNumber) || newPrice <= 0) return false;
        Room room = RoomFileHandler.findByRoomNumber(roomNumber.trim());
        if (room == null) return false;
        room.setPricePerNight(newPrice);
        return RoomFileHandler.updateRoom(room);
    }

    public boolean updateRoomStatus(String roomNumber, String newStatus) {
        if (!ValidationUtils.isNotEmpty(roomNumber)) return false;
        if (!ValidationUtils.isValidRoomStatus(newStatus)) return false;
        Room room = RoomFileHandler.findByRoomNumber(roomNumber.trim());
        if (room == null) return false;
        room.setStatus(newStatus.toUpperCase());
        return RoomFileHandler.updateRoom(room);
    }

    public boolean updateRoom(String roomNumber, String type, double price,
                              int floor, String description) {
        if (!ValidationUtils.isNotEmpty(roomNumber)) return false;
        if (!ValidationUtils.isValidRoomType(type)) return false;
        if (price <= 0) return false;
        Room room = RoomFileHandler.findByRoomNumber(roomNumber.trim());
        if (room == null) return false;
        room.setType(type.toUpperCase());
        room.setPricePerNight(price);
        room.setFloorNumber(floor);
        room.setDescription(ValidationUtils.sanitize(description));
        return RoomFileHandler.updateRoom(room);
    }

    public boolean deleteRoom(String roomNumber) {
        if (!ValidationUtils.isNotEmpty(roomNumber)) return false;
        Room room = RoomFileHandler.findByRoomNumber(roomNumber.trim());
        if (room == null) return false;
        if (Room.STATUS_BOOKED.equals(room.getStatus())) return false;
        return RoomFileHandler.deleteRoom(roomNumber.trim());
    }

    public void seedSampleRooms() {
        if (!RoomFileHandler.loadAllRooms().isEmpty()) return;
        addRoom("101", Room.TYPE_SINGLE,  50.0, 1, "Cozy single room with garden view");
        addRoom("102", Room.TYPE_SINGLE,  55.0, 1, "Single room with pool view");
        addRoom("201", Room.TYPE_DOUBLE,  90.0, 2, "Double room with balcony");
        addRoom("202", Room.TYPE_DOUBLE,  95.0, 2, "Double room with sea view");
        addRoom("301", Room.TYPE_DELUXE, 150.0, 3, "Deluxe room with king bed");
        addRoom("401", Room.TYPE_SUITE,  250.0, 4, "Luxury suite with jacuzzi");
        System.out.println("[OK] Sample rooms seeded.");
    }
}