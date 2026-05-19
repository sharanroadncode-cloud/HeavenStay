package com.heavenstay.controller;
import com.heavenstay.models.Customer;
import com.heavenstay.models.Booking;
import com.heavenstay.models.Payment;
import com.heavenstay.models.Room;
import com.heavenstay.models.User;
import com.heavenstay.service.BookingService;
import com.heavenstay.service.PaymentService;
import com.heavenstay.service.ReceptionService;
import com.heavenstay.service.RoomService;
import com.heavenstay.service.UserService;
import com.heavenstay.utils.DateUtils;
import com.heavenstay.utils.SessionUtils;
import com.heavenstay.utils.ValidationUtils;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/reception")
public class ReceptionistController {

    @Autowired private BookingService   bookingService;
    @Autowired private PaymentService   paymentService;
    @Autowired private ReceptionService receptionService;
    @Autowired private RoomService      roomService;
    @Autowired private UserService      userService;

    private User getReceptionist(HttpSession session) {
        if (!SessionUtils.isReceptionist(session)) return null;
        return SessionUtils.getUser(session);
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User receptionist = getReceptionist(session);
        if (receptionist == null) return "redirect:/login";

        String today    = DateUtils.today();
        List<Room> all  = roomService.getAllRooms();

        long available = 0, booked = 0, cleaning = 0, maintenance = 0;
        for (Room r : all) {
            if (r.getStatus() == null) continue;
            switch (r.getStatus()) {
                case Room.STATUS_AVAILABLE:   available++;   break;
                case Room.STATUS_BOOKED:      booked++;      break;
                case Room.STATUS_CLEANING:    cleaning++;    break;
                case Room.STATUS_MAINTENANCE: maintenance++; break;
            }
        }

        model.addAttribute("receptionist", receptionist);
        model.addAttribute("today",        today);
        model.addAttribute("checkIns",     bookingService.getTodaysCheckIns(today));
        model.addAttribute("checkOuts",    bookingService.getTodaysCheckOuts(today));
        model.addAttribute("available",    available);
        model.addAttribute("booked",       booked);
        model.addAttribute("cleaning",     cleaning);
        model.addAttribute("maintenance",  maintenance);
        return "receptionist/dashboard";
    }

    @GetMapping("/checkin")
    public String checkInForm(HttpSession session, Model model) {
        User receptionist = getReceptionist(session);
        if (receptionist == null) return "redirect:/login";

        model.addAttribute("receptionist",  receptionist);
        model.addAttribute("todayCheckIns",
                bookingService.getTodaysCheckIns(DateUtils.today()));
        return "receptionist/checkin";
    }

    @PostMapping("/checkin")
    public String processCheckIn(
            HttpSession session,
            @RequestParam String bookingId,
            RedirectAttributes ra) {

        User receptionist = getReceptionist(session);
        if (receptionist == null) return "redirect:/login";

        if (!ValidationUtils.isNotEmpty(bookingId)) {
            ra.addFlashAttribute("error", "Booking ID is required.");
            return "redirect:/reception/checkin";
        }

        String bid = bookingId.trim();
        Booking booking = bookingService.getBookingById(bid);
        if (booking == null) {
            ra.addFlashAttribute("error", "Booking not found: " + bid);
            return "redirect:/reception/checkin";
        }

        boolean ok = receptionService.checkIn(bid);
        if (!ok) {
            String reason = !paymentService.isAlreadyPaid(bid)
                    ? "Payment required before check-in."
                    : "Cannot check in. Status: " + booking.getStatus();
            ra.addFlashAttribute("error", reason);
            return "redirect:/reception/checkin";
        }

        ra.addFlashAttribute("success",
                "Guest checked in. Booking: " + bid +
                        " | Room: " + booking.getRoomNumber());
        return "redirect:/reception/checkin";
    }

    @GetMapping("/checkout")
    public String checkOutForm(HttpSession session, Model model) {
        User receptionist = getReceptionist(session);
        if (receptionist == null) return "redirect:/login";

        model.addAttribute("receptionist",   receptionist);
        model.addAttribute("todayCheckOuts",
                bookingService.getTodaysCheckOuts(DateUtils.today()));
        return "receptionist/checkout";
    }

    @PostMapping("/checkout")
    public String processCheckOut(
            HttpSession session,
            @RequestParam String bookingId,
            RedirectAttributes ra) {

        User receptionist = getReceptionist(session);
        if (receptionist == null) return "redirect:/login";

        if (!ValidationUtils.isNotEmpty(bookingId)) {
            ra.addFlashAttribute("error", "Booking ID is required.");
            return "redirect:/reception/checkout";
        }

        String bid = bookingId.trim();
        Booking booking = bookingService.getBookingById(bid);
        if (booking == null) {
            ra.addFlashAttribute("error", "Booking not found: " + bid);
            return "redirect:/reception/checkout";
        }

        boolean ok = receptionService.checkOut(bid);
        if (!ok) {
            ra.addFlashAttribute("error",
                    "Cannot check out. Status: " + booking.getStatus());
            return "redirect:/reception/checkout";
        }

        ra.addFlashAttribute("success",
                "Guest checked out. Room " +
                        booking.getRoomNumber() + " set to CLEANING.");
        return "redirect:/reception/checkout";
    }

    @GetMapping("/walkin")
    public String walkInForm(HttpSession session, Model model) {
        User receptionist = getReceptionist(session);
        if (receptionist == null) return "redirect:/login";

        model.addAttribute("receptionist",   receptionist);
        model.addAttribute("availableRooms", roomService.getAvailableRooms());
        model.addAttribute("customers",      userService.getAllCustomers());
        model.addAttribute("today",          DateUtils.today());
        return "receptionist/walkin";
    }

    @PostMapping("/walkin")
    public String processWalkIn(
            HttpSession session,
            @RequestParam String customerId,
            @RequestParam String roomNumber,
            @RequestParam String checkIn,
            @RequestParam String checkOut,
            @RequestParam String paymentMethod,
            RedirectAttributes ra) {

        User receptionist = getReceptionist(session);
        if (receptionist == null) return "redirect:/login";

        if (!DateUtils.isValidDate(checkIn) || !DateUtils.isValidDate(checkOut)) {
            ra.addFlashAttribute("error", "Invalid date format.");
            return "redirect:/reception/walkin";
        }
        if (!DateUtils.isAfter(checkOut, checkIn)) {
            ra.addFlashAttribute("error", "Check-out must be after check-in.");
            return "redirect:/reception/walkin";
        }
        if (!ValidationUtils.isValidPaymentMethod(paymentMethod)) {
            ra.addFlashAttribute("error", "Invalid payment method.");
            return "redirect:/reception/walkin";
        }

        Booking booking = bookingService.createBooking(
                customerId.trim(), roomNumber.trim(), checkIn.trim(), checkOut.trim()
        );
        if (booking == null) {
            ra.addFlashAttribute("error",
                    "Walk-in booking failed. Check availability and dates.");
            return "redirect:/reception/walkin";
        }

        Payment payment = paymentService.processPayment(
                booking.getBookingId(),
                customerId.trim(),
                paymentMethod.toUpperCase()
        );
        if (payment == null) {
            ra.addFlashAttribute("error",
                    "Booking created (ID: " + booking.getBookingId() +
                            ") but payment failed. Process manually.");
            return "redirect:/reception/dashboard";
        }

        ra.addFlashAttribute("success",
                "Walk-in complete! Booking: " + booking.getBookingId() +
                        " | Room: " + roomNumber +
                        " | Paid: LKR " + booking.getTotalPrice());
        return "redirect:/reception/dashboard";
    }

    @GetMapping("/bookings")
    public String allBookings(
            HttpSession session,
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) String search,
            Model model) {

        User receptionist = getReceptionist(session);
        if (receptionist == null) return "redirect:/login";

        List<Booking> bookings;
        if ("active".equals(filter)) {
            bookings = bookingService.getActiveBookings();
        } else if (ValidationUtils.isNotEmpty(search)) {
            bookings = bookingService.getBookingsByCustomer(search.trim());
            if (bookings.isEmpty()) {
                Booking single = bookingService.getBookingById(search.trim());
                bookings = new ArrayList<>();
                if (single != null) bookings.add(single);
            }
        } else {
            bookings = bookingService.getAllBookings();
        }

        model.addAttribute("receptionist", receptionist);
        model.addAttribute("bookings",     bookings);
        model.addAttribute("filter",       filter);
        model.addAttribute("search",       search);
        return "receptionist/bookings";
    }

    @GetMapping("/rooms")
    public String roomStatus(HttpSession session, Model model) {
        User receptionist = getReceptionist(session);
        if (receptionist == null) return "redirect:/login";

        List<Room> allRooms = roomService.getAllRooms();
        List<Room> cleaning = new ArrayList<>();
        for (Room r : allRooms) {
            if (Room.STATUS_CLEANING.equals(r.getStatus())) cleaning.add(r);
        }

        model.addAttribute("receptionist",  receptionist);
        model.addAttribute("allRooms",      allRooms);
        model.addAttribute("cleaningRooms", cleaning);
        return "receptionist/rooms";
    }

    @PostMapping("/rooms/available")
    public String markAvailable(
            HttpSession session,
            @RequestParam String roomNumber,
            RedirectAttributes ra) {

        User receptionist = getReceptionist(session);
        if (receptionist == null) return "redirect:/login";

        boolean ok = receptionService.markRoomAvailable(roomNumber.trim());
        ra.addFlashAttribute(
                ok ? "success" : "error",
                ok ? "Room " + roomNumber + " marked as AVAILABLE."
                        : "Could not update room " + roomNumber + "."
        );
        return "redirect:/reception/rooms";
    }

    @PostMapping("/rooms/maintenance")
    public String markMaintenance(
            HttpSession session,
            @RequestParam String roomNumber,
            RedirectAttributes ra) {

        User receptionist = getReceptionist(session);
        if (receptionist == null) return "redirect:/login";

        boolean ok = receptionService.markRoomMaintenance(roomNumber.trim());
        ra.addFlashAttribute(
                ok ? "success" : "error",
                ok ? "Room " + roomNumber + " set to MAINTENANCE."
                        : "Could not update room " + roomNumber + "."
        );
        return "redirect:/reception/rooms";
    }

    @GetMapping("/payment")
    public String paymentForm(HttpSession session, Model model) {
        User receptionist = getReceptionist(session);
        if (receptionist == null) return "redirect:/login";

        List<Booking> active = bookingService.getActiveBookings();
        List<Booking> unpaid = new ArrayList<>();
        for (Booking b : active) {
            if (!paymentService.isAlreadyPaid(b.getBookingId())) unpaid.add(b);
        }

        model.addAttribute("receptionist",   receptionist);
        model.addAttribute("unpaidBookings", unpaid);
        return "receptionist/payment";
    }

    @PostMapping("/payment")
    public String processPayment(
            HttpSession session,
            @RequestParam String bookingId,
            @RequestParam String customerId,
            @RequestParam String paymentMethod,
            RedirectAttributes ra) {

        User receptionist = getReceptionist(session);
        if (receptionist == null) return "redirect:/login";

        if (!ValidationUtils.isValidPaymentMethod(paymentMethod)) {
            ra.addFlashAttribute("error", "Invalid payment method.");
            return "redirect:/reception/payment";
        }

        Payment payment = paymentService.processPayment(
                bookingId.trim(),
                customerId.trim(),
                paymentMethod.toUpperCase()
        );

        ra.addFlashAttribute(
                payment != null ? "success" : "error",
                payment != null
                        ? "Payment processed! ID: " + payment.getPaymentId() +
                          " | LKR " + payment.getAmount()
                        : "Payment failed. Check booking and customer ID."
        );
        return "redirect:/reception/payment";
    }

    @GetMapping("/customer-lookup")
    public String customerLookup(
            HttpSession session,
            @RequestParam(required = false) String customerId,
            Model model) {

        User receptionist = getReceptionist(session);
        if (receptionist == null) return "redirect:/login";

        Customer customer = null;
        List<Booking> bookings = new ArrayList<>();

        if (ValidationUtils.isNotEmpty(customerId)) {
            User foundUser = userService.findById(customerId.trim());

            if (foundUser instanceof Customer) {
                customer = (Customer) foundUser;
                bookings = bookingService.getBookingsByCustomer(customerId.trim());
            } else if (foundUser != null) {
                model.addAttribute("error", "This ID belongs to " + foundUser.getRole() + ", not a customer.");
            } else {
                model.addAttribute("error", "No customer found with ID: " + customerId);
            }
        }

        model.addAttribute("receptionist", receptionist);
        model.addAttribute("customer", customer);
        model.addAttribute("bookings", bookings);
        model.addAttribute("searchId", customerId);

        return "receptionist/customer-lookup";
    }
}