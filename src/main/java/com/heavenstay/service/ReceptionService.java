package com.heavenstay.service;

import com.heavenstay.filehandler.RoomFileHandler;
import com.heavenstay.models.Booking;
import com.heavenstay.models.Room;
import com.heavenstay.utils.ValidationUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class ReceptionService {

    private final BookingService bookingService;
    private final PaymentService paymentService;

    public ReceptionService(
            @Lazy BookingService bookingService,
            @Lazy PaymentService paymentService) {
        this.bookingService = bookingService;
        this.paymentService = paymentService;
    }

    public boolean checkIn(String bookingId) {
        if (!ValidationUtils.isNotEmpty(bookingId)) return false;

        Booking booking = bookingService.getBookingById(bookingId.trim());
        if (booking == null) return false;

        if (!Booking.STATUS_CONFIRMED.equals(booking.getStatus())) return false;
        if (!paymentService.isAlreadyPaid(bookingId.trim())) return false;

        return true;
    }

    public boolean checkOut(String bookingId) {
        if (!ValidationUtils.isNotEmpty(bookingId)) return false;
        return bookingService.completeBooking(bookingId.trim());
    }

    public boolean markRoomAvailable(String roomNumber) {
        if (!ValidationUtils.isNotEmpty(roomNumber)) return false;

        Room room = RoomFileHandler.findByRoomNumber(roomNumber.trim());
        if (room == null) return false;

        if (Room.STATUS_BOOKED.equals(room.getStatus())) return false;

        room.setStatus(Room.STATUS_AVAILABLE);
        return RoomFileHandler.updateRoom(room);
    }

    public boolean markRoomMaintenance(String roomNumber) {
        if (!ValidationUtils.isNotEmpty(roomNumber)) return false;

        Room room = RoomFileHandler.findByRoomNumber(roomNumber.trim());
        if (room == null) return false;

        if (Room.STATUS_BOOKED.equals(room.getStatus())) return false;

        room.setStatus(Room.STATUS_MAINTENANCE);
        return RoomFileHandler.updateRoom(room);
    }

    public boolean markRoomCleaning(String roomNumber) {
        if (!ValidationUtils.isNotEmpty(roomNumber)) return false;

        Room room = RoomFileHandler.findByRoomNumber(roomNumber.trim());
        if (room == null) return false;

        if (Room.STATUS_BOOKED.equals(room.getStatus())) return false;

        room.setStatus(Room.STATUS_CLEANING);
        return RoomFileHandler.updateRoom(room);
    }
}