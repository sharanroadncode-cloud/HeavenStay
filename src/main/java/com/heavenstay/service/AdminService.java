package com.heavenstay.service;

import com.heavenstay.filehandler.BookingFileHandler;
import com.heavenstay.filehandler.PaymentFileHandler;
import com.heavenstay.filehandler.RoomFileHandler;
import com.heavenstay.filehandler.UserFileHandler;
import com.heavenstay.models.Booking;
import com.heavenstay.models.Payment;
import com.heavenstay.models.Room;
import com.heavenstay.models.User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminService {

    public Map<String, Object> getSystemStats() {
        Map<String, Object> stats = new HashMap<>();

        List<User>    users    = UserFileHandler.loadAllUsers();
        List<Room>    rooms    = RoomFileHandler.loadAllRooms();
        List<Booking> bookings = BookingFileHandler.loadAllBookings();
        List<Payment> payments = PaymentFileHandler.loadAllPayments();

        long admins = 0, receptionists = 0, customers = 0;
        for (User u : users) {
            if (u == null || u.getRole() == null) continue;
            switch (u.getRole()) {
                case "ADMIN":        admins++;        break;
                case "RECEPTIONIST": receptionists++; break;
                case "CUSTOMER":     customers++;     break;
            }
        }

        long available = 0, booked = 0, cleaning = 0, maintenance = 0;
        for (Room r : rooms) {
            if (r == null || r.getStatus() == null) continue;
            switch (r.getStatus()) {
                case Room.STATUS_AVAILABLE:   available++;   break;
                case Room.STATUS_BOOKED:      booked++;      break;
                case Room.STATUS_CLEANING:    cleaning++;    break;
                case Room.STATUS_MAINTENANCE: maintenance++; break;
            }
        }

        long confirmed = 0, completed = 0, cancelled = 0, pending = 0;
        for (Booking b : bookings) {
            if (b == null || b.getStatus() == null) continue;
            switch (b.getStatus()) {
                case Booking.STATUS_CONFIRMED: confirmed++; break;
                case Booking.STATUS_COMPLETED: completed++; break;
                case Booking.STATUS_CANCELLED: cancelled++; break;
                case Booking.STATUS_PENDING:   pending++;   break;
            }
        }

        double totalRevenue = 0, cashRevenue = 0,
                cardRevenue  = 0, onlineRevenue = 0;
        for (Payment p : payments) {
            if (p == null || !Payment.STATUS_PAID.equals(p.getStatus())) continue;
            totalRevenue += p.getAmount();
            if (p.getPaymentMethod() == null) continue;
            switch (p.getPaymentMethod()) {
                case Payment.METHOD_CASH:   cashRevenue   += p.getAmount(); break;
                case Payment.METHOD_CARD:   cardRevenue   += p.getAmount(); break;
                case Payment.METHOD_ONLINE: onlineRevenue += p.getAmount(); break;
            }
        }

        stats.put("totalUsers",    users.size());
        stats.put("admins",        admins);
        stats.put("receptionists", receptionists);
        stats.put("customers",     customers);
        stats.put("totalRooms",    rooms.size());
        stats.put("available",     available);
        stats.put("booked",        booked);
        stats.put("cleaning",      cleaning);
        stats.put("maintenance",   maintenance);
        stats.put("totalBookings", bookings.size());
        stats.put("confirmed",     confirmed);
        stats.put("completed",     completed);
        stats.put("cancelled",     cancelled);
        stats.put("pending",       pending);
        stats.put("totalPayments", payments.size());
        stats.put("totalRevenue",  totalRevenue);
        stats.put("cashRevenue",   cashRevenue);
        stats.put("cardRevenue",   cardRevenue);
        stats.put("onlineRevenue", onlineRevenue);

        return stats;
    }
}