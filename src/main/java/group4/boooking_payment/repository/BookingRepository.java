package group4.boooking_payment.repository;

import group4.boooking_payment.model.Booking;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class BookingRepository {

    // ─── In-memory store (replaces the database) ──────────────────────────────
    private final Map<String, Booking> store = new HashMap<>();

    // Pre-loaded sample data so the app works immediately on first run
    public BookingRepository() {
        seedData();
    }

    // ─── CRUD ─────────────────────────────────────────────────────────────────

    public Booking save(Booking booking) {
        store.put(booking.getBookingId(), booking);
        return booking;
    }

    public Optional<Booking> findById(String bookingId) {
        return Optional.ofNullable(store.get(bookingId));
    }

    public List<Booking> findAll() {
        return new ArrayList<>(store.values());
    }

    public void deleteById(String bookingId) {
        store.remove(bookingId);
    }

    public boolean existsById(String bookingId) {
        return store.containsKey(bookingId);
    }

    // ─── Custom Queries (stream-based) ────────────────────────────────────────

    public List<Booking> findByGuestId(String guestId) {
        return store.values().stream()
                .filter(b -> guestId.equals(b.getGuestId()))
                .collect(Collectors.toList());
    }

    public List<Booking> findByStatus(String status) {
        return store.values().stream()
                .filter(b -> status.equals(b.getStatus()))
                .collect(Collectors.toList());
    }

    public List<Booking> findByRoomId(String roomId) {
        return store.values().stream()
                .filter(b -> roomId.equals(b.getRoomId()))
                .collect(Collectors.toList());
    }

    // Check if a room is already booked for overlapping dates
    public List<Booking> findOverlappingBookings(String roomId, String checkIn, String checkOut) {
        return store.values().stream()
                .filter(b -> roomId.equals(b.getRoomId()))
                .filter(b -> !"CANCELLED".equals(b.getStatus()))
                .filter(b -> b.getCheckInDate().compareTo(checkOut) < 0
                        && b.getCheckOutDate().compareTo(checkIn) > 0)
                .collect(Collectors.toList());
    }

    // ─── Seed Data ────────────────────────────────────────────────────────────
    // Sample bookings loaded at startup so pages show real data immediately

    private void seedData() {
        Booking b1 = new Booking();
        b1.setBookingId("BK-A1B2C3");
        b1.setGuestId("G001");
        b1.setRoomId("R002");
        b1.setCheckInDate("2026-05-10");
        b1.setCheckOutDate("2026-05-13");
        b1.setStatus("CONFIRMED");
        b1.setNumberOfGuests(2);
        b1.setSpecialRequests("Sea view preferred");
        b1.setSubtotal(75000.00);
        b1.setDiscount(3750.00);
        b1.setTax(7125.00);
        b1.setTotal(78375.00);
        store.put(b1.getBookingId(), b1);

        Booking b2 = new Booking();
        b2.setBookingId("BK-D4E5F6");
        b2.setGuestId("G001");
        b2.setRoomId("R001");
        b2.setCheckInDate("2026-04-01");
        b2.setCheckOutDate("2026-04-03");
        b2.setStatus("CHECKED_OUT");
        b2.setNumberOfGuests(1);
        b2.setSpecialRequests("");
        b2.setSubtotal(30000.00);
        b2.setDiscount(1500.00);
        b2.setTax(2850.00);
        b2.setTotal(31350.00);
        store.put(b2.getBookingId(), b2);

        Booking b3 = new Booking();
        b3.setBookingId("BK-G7H8I9");
        b3.setGuestId("G002");
        b3.setRoomId("R003");
        b3.setCheckInDate("2026-06-20");
        b3.setCheckOutDate("2026-06-23");
        b3.setStatus("PENDING");
        b3.setNumberOfGuests(3);
        b3.setSpecialRequests("Anniversary decoration");
        b3.setSubtotal(150000.00);
        b3.setDiscount(7500.00);
        b3.setTax(14250.00);
        b3.setTotal(156750.00);
        store.put(b3.getBookingId(), b3);
    }
}
