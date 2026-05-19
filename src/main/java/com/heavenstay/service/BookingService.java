package com.heavenstay.service;

import com.heavenstay.filehandler.BookingFileHandler;
import com.heavenstay.filehandler.RoomFileHandler;
import com.heavenstay.filehandler.UserFileHandler;
import com.heavenstay.models.Booking;
import com.heavenstay.models.Customer;
import com.heavenstay.models.Room;
import com.heavenstay.models.User;
import com.heavenstay.utils.DateUtils;
import com.heavenstay.utils.IDGenerator;
import com.heavenstay.utils.ValidationUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class BookingService {

    public Booking createBooking(String customerId, String roomNumber,
                                 String checkIn, String checkOut) {
        if (!ValidationUtils.isNotEmpty(customerId)) return null;
        if (!ValidationUtils.isNotEmpty(roomNumber)) return null;
        if (!DateUtils.isValidDate(checkIn)) return null;
        if (!DateUtils.isValidDate(checkOut)) return null;

        User user = UserFileHandler.findById(customerId.trim());
        if (user == null || !"CUSTOMER".equals(user.getRole())) return null;

        Room room = RoomFileHandler.findByRoomNumber(roomNumber.trim());
        if (room == null || !room.isAvailable()) return null;

        int nights = DateUtils.calculateNights(checkIn, checkOut);
        if (nights <= 0) return null;

        if (hasOverlap(roomNumber.trim(), checkIn, checkOut)) return null;

        double totalPrice = nights * room.getPricePerNight();
        String bookingId  = IDGenerator.generateBookingId();

        Booking booking = new Booking(
                bookingId,
                customerId.trim(),
                roomNumber.trim(),
                checkIn.trim(),
                checkOut.trim(),
                nights,
                totalPrice,
                Booking.STATUS_CONFIRMED,
                DateUtils.today()
        );

        boolean saved = BookingFileHandler.appendBooking(booking);
        if (!saved) return null;

        room.setStatus(Room.STATUS_BOOKED);
        RoomFileHandler.updateRoom(room);

        if (user instanceof Customer) {
            Customer customer = (Customer) user;
            customer.addLoyaltyPoints((int) totalPrice);
            UserFileHandler.updateUser(customer);
        }

        return booking;
    }

    public boolean cancelBooking(String bookingId, String requesterId) {
        if (!ValidationUtils.isNotEmpty(bookingId)) return false;
        if (!ValidationUtils.isNotEmpty(requesterId)) return false;

        Booking booking = BookingFileHandler.findById(bookingId.trim());
        if (booking == null) return false;

        User requester = UserFileHandler.findById(requesterId.trim());
        if (requester == null) return false;

        boolean isOwner = booking.getCustomerId().equals(requesterId.trim());
        boolean isStaff = "ADMIN".equals(requester.getRole()) ||
                "RECEPTIONIST".equals(requester.getRole());

        if (!isOwner && !isStaff) return false;
        if (Booking.STATUS_COMPLETED.equals(booking.getStatus())) return false;
        if (Booking.STATUS_CANCELLED.equals(booking.getStatus())) return false;

        booking.setStatus(Booking.STATUS_CANCELLED);
        boolean updated = BookingFileHandler.updateBooking(booking);
        if (!updated) return false;

        Room room = RoomFileHandler.findByRoomNumber(booking.getRoomNumber());
        if (room != null && Room.STATUS_BOOKED.equals(room.getStatus())) {
            room.setStatus(Room.STATUS_AVAILABLE);
            RoomFileHandler.updateRoom(room);
        }

        return true;
    }

    public boolean completeBooking(String bookingId) {
        if (!ValidationUtils.isNotEmpty(bookingId)) return false;

        Booking booking = BookingFileHandler.findById(bookingId.trim());
        if (booking == null) return false;
        if (!Booking.STATUS_CONFIRMED.equals(booking.getStatus())) return false;

        booking.setStatus(Booking.STATUS_COMPLETED);
        boolean updated = BookingFileHandler.updateBooking(booking);
        if (!updated) return false;

        Room room = RoomFileHandler.findByRoomNumber(booking.getRoomNumber());
        if (room != null) {
            room.setStatus(Room.STATUS_CLEANING);
            RoomFileHandler.updateRoom(room);
        }

        return true;
    }

    public List<Booking> getAllBookings() {
        return BookingFileHandler.loadAllBookings();
    }

    public List<Booking> getBookingsByCustomer(String customerId) {
        if (!ValidationUtils.isNotEmpty(customerId)) return new ArrayList<>();
        return BookingFileHandler.findByCustomerId(customerId.trim());
    }

    public List<Booking> getActiveBookings() {
        return BookingFileHandler.findByStatus(Booking.STATUS_CONFIRMED);
    }

    public List<Booking> getBookingsByRoom(String roomNumber) {
        if (!ValidationUtils.isNotEmpty(roomNumber)) return new ArrayList<>();
        return BookingFileHandler.findByRoomNumber(roomNumber.trim());
    }

    public Booking getBookingById(String bookingId) {
        if (!ValidationUtils.isNotEmpty(bookingId)) return null;
        return BookingFileHandler.findById(bookingId.trim());
    }

    public List<Booking> getTodaysCheckIns(String today) {
        List<Booking> result = new ArrayList<>();
        if (!ValidationUtils.isNotEmpty(today)) return result;
        for (Booking b : BookingFileHandler.loadAllBookings()) {
            if (today.equals(b.getCheckInDate()) &&
                    Booking.STATUS_CONFIRMED.equals(b.getStatus())) {
                result.add(b);
            }
        }
        return result;
    }

    public List<Booking> getTodaysCheckOuts(String today) {
        List<Booking> result = new ArrayList<>();
        if (!ValidationUtils.isNotEmpty(today)) return result;
        for (Booking b : BookingFileHandler.loadAllBookings()) {
            if (today.equals(b.getCheckOutDate()) &&
                    Booking.STATUS_CONFIRMED.equals(b.getStatus())) {
                result.add(b);
            }
        }
        return result;
    }

    private boolean hasOverlap(String roomNumber, String newIn, String newOut) {
        LocalDate nIn  = DateUtils.parse(newIn);
        LocalDate nOut = DateUtils.parse(newOut);
        if (nIn == null || nOut == null) return false;

        for (Booking b : BookingFileHandler.findByRoomNumber(roomNumber)) {
            if (Booking.STATUS_CANCELLED.equals(b.getStatus()) ||
                    Booking.STATUS_COMPLETED.equals(b.getStatus())) continue;

            LocalDate eIn  = DateUtils.parse(b.getCheckInDate());
            LocalDate eOut = DateUtils.parse(b.getCheckOutDate());
            if (eIn == null || eOut == null) continue;

            if (nIn.isBefore(eOut) && nOut.isAfter(eIn)) return true;
        }
        return false;
    }
}