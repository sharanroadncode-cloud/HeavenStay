package com.heavenstay.filehandler;

import com.heavenstay.models.Booking;
import java.util.ArrayList;
import java.util.List;

public class BookingFileHandler {

    private static final String FILE_PATH = "data/bookings.txt";

    public static List<Booking> loadAllBookings() {
        List<String> lines = FileManager.readLines(FILE_PATH);
        List<Booking> bookings = new ArrayList<>();
        for (String line : lines) {
            try {
                bookings.add(Booking.fromFileString(line));
            } catch (Exception e) {
                System.out.println("[WARN] Skipping booking: " + line);
            }
        }
        return bookings;
    }

    public static boolean saveAllBookings(List<Booking> bookings) {
        List<String> lines = new ArrayList<>();
        for (Booking b : bookings) lines.add(b.toFileString());
        return FileManager.writeLines(FILE_PATH, lines);
    }

    public static boolean appendBooking(Booking booking) {
        if (booking == null) return false;
        return FileManager.appendLine(FILE_PATH, booking.toFileString());
    }

    public static Booking findById(String bookingId) {
        if (bookingId == null || bookingId.trim().isEmpty()) return null;
        for (Booking b : loadAllBookings()) {
            if (b.getBookingId().equals(bookingId.trim())) return b;
        }
        return null;
    }

    public static List<Booking> findByCustomerId(String customerId) {
        List<Booking> result = new ArrayList<>();
        if (customerId == null || customerId.trim().isEmpty()) return result;
        for (Booking b : loadAllBookings()) {
            if (b.getCustomerId().equals(customerId.trim())) result.add(b);
        }
        return result;
    }

    public static List<Booking> findByRoomNumber(String roomNumber) {
        List<Booking> result = new ArrayList<>();
        if (roomNumber == null || roomNumber.trim().isEmpty()) return result;
        for (Booking b : loadAllBookings()) {
            if (b.getRoomNumber().equals(roomNumber.trim())) result.add(b);
        }
        return result;
    }

    public static boolean updateBooking(Booking updated) {
        if (updated == null) return false;
        List<Booking> bookings = loadAllBookings();
        boolean found = false;
        for (int i = 0; i < bookings.size(); i++) {
            if (bookings.get(i).getBookingId().equals(updated.getBookingId())) {
                bookings.set(i, updated);
                found = true;
                break;
            }
        }
        if (!found) return false;
        return saveAllBookings(bookings);
    }

    public static boolean cancelBooking(String bookingId) {
        if (bookingId == null || bookingId.trim().isEmpty()) return false;
        List<Booking> bookings = loadAllBookings();
        for (Booking b : bookings) {
            if (b.getBookingId().equals(bookingId.trim())) {
                b.setStatus(Booking.STATUS_CANCELLED);
                return saveAllBookings(bookings);
            }
        }
        return false;
    }

    public static List<Booking> findByStatus(String status) {
        List<Booking> result = new ArrayList<>();
        if (status == null || status.trim().isEmpty()) return result;
        for (Booking b : loadAllBookings()) {
            if (b.getStatus().equals(status.trim())) result.add(b);
        }
        return result;
    }
}