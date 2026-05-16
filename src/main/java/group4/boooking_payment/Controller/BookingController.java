package group4.boooking_payment.Controller;

import group4.boooking_payment.model.Booking;
import group4.boooking_payment.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/Booking")
@CrossOrigin(origins = "*")   // allows the HTML frontend to call this API
public class BookingController {

    @Autowired
    private BookingService bookingService;

        // POST /api/bookings  →  create new booking
        @PostMapping
        public ResponseEntity<?> createBooking(@RequestBody Booking booking) {
            try {
                Booking created = bookingService.createBooking(booking);
                return ResponseEntity.status(HttpStatus.CREATED).body(created);
            } catch (RuntimeException e) {
                return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
            }
        }

        // GET /api/bookings  →  list all (optional ?status=CONFIRMED or ?guestId=G001)
        @GetMapping
        public ResponseEntity<List<Booking>> getAllBookings(
                @RequestParam(required = false) String status,
                @RequestParam(required = false) String guestId) {

            List<Booking> result;
            if (status != null && !status.isEmpty()) {
                result = bookingService.getBookingsByStatus(status);
            } else if (guestId != null && !guestId.isEmpty()) {
                result = bookingService.getBookingsByGuest(guestId);
            } else {
                result = bookingService.getAllBookings();
            }
            return ResponseEntity.ok(result);
        }

        // GET /api/bookings/{id}  →  get one booking
        @GetMapping("/{id}")
        public ResponseEntity<?> getBookingById(@PathVariable String id) {
            return bookingService.getBookingById(id)
                    .<ResponseEntity<?>>map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }

        // PUT /api/bookings/{id}  →  update booking dates / guests
        @PutMapping("/{id}")
        public ResponseEntity<?> updateBooking(@PathVariable String id,
                                               @RequestBody Booking updatedData) {
            try {
                return ResponseEntity.ok(bookingService.updateBooking(id, updatedData));
            } catch (RuntimeException e) {
                return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
            }
        }

        // PUT /api/bookings/{id}/confirm  →  confirm a booking
        @PutMapping("/{id}/confirm")
        public ResponseEntity<?> confirmBooking(@PathVariable String id) {
            try {
                return ResponseEntity.ok(bookingService.confirmBooking(id));
            } catch (RuntimeException e) {
                return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
            }
        }

        // PUT /api/bookings/{id}/cancel  →  cancel a booking
        @PutMapping("/{id}/cancel")
        public ResponseEntity<?> cancelBooking(@PathVariable String id) {
            try {
                return ResponseEntity.ok(bookingService.cancelBooking(id));
            } catch (RuntimeException e) {
                return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
            }
        }

        // DELETE /api/bookings/{id}  →  remove a booking
        @DeleteMapping("/{id}")
        public ResponseEntity<?> deleteBooking(@PathVariable String id) {
            try {
                bookingService.deleteBooking(id);
                return ResponseEntity.ok(Map.of("message", "Booking " + id + " deleted."));
            } catch (RuntimeException e) {
                return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
            }
        }

        // GET /api/bookings/availability?roomId=R001&checkIn=2026-06-01&checkOut=2026-06-05
        @GetMapping("/availability")
        public ResponseEntity<Map<String, Boolean>> checkAvailability(
                @RequestParam String roomId,
                @RequestParam String checkIn,
                @RequestParam String checkOut) {
            boolean available = bookingService.isRoomAvailable(roomId, checkIn, checkOut);
            return ResponseEntity.ok(Map.of("available", available));
         }

}


