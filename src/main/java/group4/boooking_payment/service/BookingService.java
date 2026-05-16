package group4.boooking_payment.service;

import group4.boooking_payment.model.Booking;
import group4.boooking_payment.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    // ─── Create Booking ───────────────────────────────────────────────────────

    public Booking createBooking(Booking booking) {
        // Generate unique booking ID
        String bookingId = "BK-" + UUID.randomUUID()
                .toString().replace("-", "").substring(0, 6).toUpperCase(Locale.ROOT);
        booking.setBookingId(bookingId);
        booking.setStatus("PENDING");

        // Run all price calculations
        booking.calculateSubtotal();
        booking.calculateDiscount();
        booking.calculateTax();
        booking.calculateTotal();

        // Check room availability
        List<Booking> overlaps = bookingRepository.findOverlappingBookings(
                booking.getRoomId(), booking.getCheckInDate(), booking.getCheckOutDate());
        if (!overlaps.isEmpty()) {
            throw new RuntimeException("Room " + booking.getRoomId()
                    + " is not available for the selected dates.");
        }

        return bookingRepository.save(booking);
    }

    // ─── Get All Bookings ─────────────────────────────────────────────────────

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    // ─── Get Single Booking ───────────────────────────────────────────────────

    public Optional<Booking> getBookingById(String bookingId) {
        return bookingRepository.findById(bookingId);
    }

    // ─── Get By Guest ─────────────────────────────────────────────────────────

    public List<Booking> getBookingsByGuest(String guestId) {
        return bookingRepository.findByGuestId(guestId);
    }

    // ─── Get By Status ────────────────────────────────────────────────────────

    public List<Booking> getBookingsByStatus(String status) {
        return bookingRepository.findByStatus(status);
    }

    // ─── Confirm Booking ──────────────────────────────────────────────────────

    public Booking confirmBooking(String bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found: " + bookingId));
        booking.confirm();
        return bookingRepository.save(booking);
    }

    // ─── Cancel Booking ───────────────────────────────────────────────────────

    public Booking cancelBooking(String bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found: " + bookingId));
        booking.cancel();
        return bookingRepository.save(booking);
    }

    // ─── Update Booking ───────────────────────────────────────────────────────

    public Booking updateBooking(String bookingId, Booking updated) {
        Booking existing = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found: " + bookingId));

        existing.setCheckInDate(updated.getCheckInDate());
        existing.setCheckOutDate(updated.getCheckOutDate());
        existing.setNumberOfGuests(updated.getNumberOfGuests());
        existing.setSpecialRequests(updated.getSpecialRequests());

        // Recalculate prices
        existing.calculateSubtotal();
        existing.calculateDiscount();
        existing.calculateTax();
        existing.calculateTotal();

        return bookingRepository.save(existing);
    }

    // ─── Delete Booking ───────────────────────────────────────────────────────

    public void deleteBooking(String bookingId) {
        bookingRepository.deleteById(bookingId);
    }

    // ─── Check Availability ───────────────────────────────────────────────────

    public boolean isRoomAvailable(String roomId, String checkIn, String checkOut) {
        return bookingRepository.findOverlappingBookings(roomId, checkIn, checkOut).isEmpty();
    }
}
