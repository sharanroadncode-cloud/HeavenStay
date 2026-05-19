package com.heavenstay.controller;

import com.heavenstay.models.Booking;
import com.heavenstay.models.Customer;
import com.heavenstay.models.Payment;
import com.heavenstay.models.Review;
import com.heavenstay.models.Room;
import com.heavenstay.models.User;
import com.heavenstay.service.BookingService;
import com.heavenstay.service.PaymentService;
import com.heavenstay.service.ReviewService;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    @Autowired private RoomService    roomService;
    @Autowired private BookingService bookingService;
    @Autowired private PaymentService paymentService;
    @Autowired private ReviewService  reviewService;
    @Autowired private UserService    userService;

    private Customer getCustomer(HttpSession session) {
        return SessionUtils.getCustomer(session);
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Customer customer = getCustomer(session);
        if (customer == null) return "redirect:/login";

        List<Booking> all = bookingService.getBookingsByCustomer(customer.getUserId());
        List<Booking> recent = all.size() > 5
                ? new ArrayList<>(all.subList(all.size() - 5, all.size()))
                : new ArrayList<>(all);

        long confirmed = 0, completed = 0;
        for (Booking b : all) {
            if (Booking.STATUS_CONFIRMED.equals(b.getStatus())) confirmed++;
            if (Booking.STATUS_COMPLETED.equals(b.getStatus())) completed++;
        }

        model.addAttribute("customer",       customer);
        model.addAttribute("recentBookings", recent);
        model.addAttribute("totalBookings",  all.size());
        model.addAttribute("confirmed",      confirmed);
        model.addAttribute("completed",      completed);
        model.addAttribute("availableRooms", roomService.getAvailableRooms().size());
        return "customer/dashboard";
    }

    @GetMapping("/rooms")
    public String browseRooms(
            HttpSession session,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String maxPrice,
            @RequestParam(required = false) String floor,
            Model model) {

        Customer customer = getCustomer(session);
        if (customer == null) return "redirect:/login";

        List<Room> rooms;
        if (ValidationUtils.isNotEmpty(type)) {
            rooms = roomService.getAvailableRoomsByType(type.toUpperCase());
        } else if (ValidationUtils.isNotEmpty(maxPrice)) {
            double price = ValidationUtils.parseDouble(maxPrice, 0);
            rooms = price > 0
                    ? roomService.getRoomsByMaxPrice(price)
                    : roomService.getAvailableRooms();
        } else if (ValidationUtils.isNotEmpty(floor)) {
            int floorNum = ValidationUtils.parseInt(floor, 0);
            rooms = floorNum > 0
                    ? roomService.getRoomsByFloor(floorNum)
                    : roomService.getAvailableRooms();
        } else {
            rooms = roomService.getAvailableRooms();
        }

        model.addAttribute("customer", customer);
        model.addAttribute("rooms",    rooms);
        model.addAttribute("filter",   type);
        return "customer/rooms";
    }

    @GetMapping("/booking/new")
    public String newBookingForm(
            HttpSession session,
            @RequestParam(required = false) String roomNumber,
            Model model) {

        Customer customer = getCustomer(session);
        if (customer == null) return "redirect:/login";

        model.addAttribute("customer",       customer);
        model.addAttribute("availableRooms", roomService.getAvailableRooms());
        model.addAttribute("selectedRoom",   roomNumber);
        model.addAttribute("today",          DateUtils.today());
        return "customer/booking-new";
    }

    @PostMapping("/booking")
    public String submitBooking(
            HttpSession session,
            @RequestParam String roomNumber,
            @RequestParam String checkIn,
            @RequestParam String checkOut,
            RedirectAttributes ra) {

        Customer customer = getCustomer(session);
        if (customer == null) return "redirect:/login";

        if (!DateUtils.isValidDate(checkIn) || !DateUtils.isValidDate(checkOut)) {
            ra.addFlashAttribute("error", "Invalid date format. Use yyyy-MM-dd.");
            return "redirect:/customer/booking/new";
        }
        if (!DateUtils.isFutureOrToday(checkIn)) {
            ra.addFlashAttribute("error", "Check-in must be today or a future date.");
            return "redirect:/customer/booking/new";
        }
        if (!DateUtils.isAfter(checkOut, checkIn)) {
            ra.addFlashAttribute("error", "Check-out must be after check-in.");
            return "redirect:/customer/booking/new";
        }

        Booking booking = bookingService.createBooking(
                customer.getUserId(), roomNumber, checkIn, checkOut
        );

        if (booking == null) {
            ra.addFlashAttribute("error",
                    "Booking failed. Room may not be available for those dates.");
            return "redirect:/customer/booking/new";
        }

        User updated = userService.findById(customer.getUserId());
        if (updated instanceof Customer) {
            SessionUtils.refreshUser(session, updated);
        }

        ra.addFlashAttribute("success",
                "Booking confirmed! ID: " + booking.getBookingId() +
                        " | Total: LKR " + booking.getTotalPrice() +
                        " | Please make your payment.");
        return "redirect:/customer/bookings";
    }

    @GetMapping("/bookings")
    public String myBookings(HttpSession session, Model model) {
        Customer customer = getCustomer(session);
        if (customer == null) return "redirect:/login";

        List<Booking> bookings =
                bookingService.getBookingsByCustomer(customer.getUserId());
        List<Boolean> paidStatus = new ArrayList<>();
        for (Booking b : bookings) {
            paidStatus.add(paymentService.isAlreadyPaid(b.getBookingId()));
        }

        model.addAttribute("customer",   customer);
        model.addAttribute("bookings",   bookings);
        model.addAttribute("paidStatus", paidStatus);
        return "customer/bookings";
    }

    @GetMapping("/booking/{bookingId}")
    public String bookingDetail(
            HttpSession session,
            @PathVariable String bookingId,
            Model model) {

        Customer customer = getCustomer(session);
        if (customer == null) return "redirect:/login";

        Booking booking = bookingService.getBookingById(bookingId);
        if (booking == null ||
                !booking.getCustomerId().equals(customer.getUserId())) {
            return "redirect:/customer/bookings";
        }

        Room    room    = roomService.getRoomByNumber(booking.getRoomNumber());
        Payment payment = paymentService.getPaymentByBooking(bookingId);

        model.addAttribute("customer", customer);
        model.addAttribute("booking",  booking);
        model.addAttribute("room",     room);
        model.addAttribute("payment",  payment);
        model.addAttribute("isPaid",   paymentService.isAlreadyPaid(bookingId));
        return "customer/booking-detail";
    }

    @GetMapping("/payment")
    public String paymentForm(
            HttpSession session,
            @RequestParam(required = false) String bookingId,
            Model model) {

        Customer customer = getCustomer(session);
        if (customer == null) return "redirect:/login";

        List<Booking> all = bookingService.getBookingsByCustomer(customer.getUserId());
        List<Booking> unpaid = new ArrayList<>();
        for (Booking b : all) {
            if (Booking.STATUS_CONFIRMED.equals(b.getStatus()) &&
                    !paymentService.isAlreadyPaid(b.getBookingId())) {
                unpaid.add(b);
            }
        }

        Booking selected = null;
        if (ValidationUtils.isNotEmpty(bookingId)) {
            Booking found = bookingService.getBookingById(bookingId);
            if (found != null &&
                    found.getCustomerId().equals(customer.getUserId())) {
                selected = found;
            }
        }

        model.addAttribute("customer",        customer);
        model.addAttribute("unpaidBookings",  unpaid);
        model.addAttribute("selectedBooking", selected);
        return "customer/payment";
    }

    @PostMapping("/payment")
    public String processPayment(
            HttpSession session,
            @RequestParam String bookingId,
            @RequestParam String paymentMethod,
            RedirectAttributes ra) {

        Customer customer = getCustomer(session);
        if (customer == null) return "redirect:/login";

        if (!ValidationUtils.isValidPaymentMethod(paymentMethod)) {
            ra.addFlashAttribute("error", "Invalid payment method.");
            return "redirect:/customer/payment";
        }

        Payment payment = paymentService.processPayment(
                bookingId, customer.getUserId(), paymentMethod.toUpperCase()
        );

        if (payment == null) {
            ra.addFlashAttribute("error",
                    "Payment failed. Booking may already be paid or invalid.");
            return "redirect:/customer/payment";
        }

        ra.addFlashAttribute("success",
                "Payment successful! ID: " + payment.getPaymentId() +
                        " | Amount: LKR " + payment.getAmount());
        return "redirect:/customer/bookings";
    }

    @PostMapping("/cancel/{bookingId}")
    public String cancelBooking(
            HttpSession session,
            @PathVariable String bookingId,
            RedirectAttributes ra) {

        Customer customer = getCustomer(session);
        if (customer == null) return "redirect:/login";

        boolean cancelled =
                bookingService.cancelBooking(bookingId, customer.getUserId());

        if (!cancelled) {
            ra.addFlashAttribute("error",
                    "Could not cancel booking " + bookingId + ".");
            return "redirect:/customer/bookings";
        }

        if (paymentService.isAlreadyPaid(bookingId)) {
            paymentService.refundPayment(bookingId);
            ra.addFlashAttribute("success",
                    "Booking cancelled and refund processed.");
        } else {
            ra.addFlashAttribute("success",
                    "Booking " + bookingId + " cancelled.");
        }
        return "redirect:/customer/bookings";
    }

    @GetMapping("/reviews")
    public String myReviews(HttpSession session, Model model) {
        Customer customer = getCustomer(session);
        if (customer == null) return "redirect:/login";

        List<Review>  myReviews  = reviewService.getReviewsByCustomer(customer.getUserId());
        List<Booking> all        = bookingService.getBookingsByCustomer(customer.getUserId());
        List<Booking> reviewable = new ArrayList<>();

        for (Booking b : all) {
            if (!Booking.STATUS_COMPLETED.equals(b.getStatus())) continue;
            boolean reviewed = false;
            for (Review r : myReviews) {
                if (r.getBookingId().equals(b.getBookingId())) {
                    reviewed = true;
                    break;
                }
            }
            if (!reviewed) reviewable.add(b);
        }

        model.addAttribute("customer",           customer);
        model.addAttribute("myReviews",          myReviews);
        model.addAttribute("reviewableBookings", reviewable);
        return "customer/reviews";
    }

    @PostMapping("/review")
    public String submitReview(
            HttpSession session,
            @RequestParam String bookingId,
            @RequestParam int    rating,
            @RequestParam String comment,
            RedirectAttributes ra) {

        Customer customer = getCustomer(session);
        if (customer == null) return "redirect:/login";

        if (!ValidationUtils.isNotEmpty(comment)) {
            ra.addFlashAttribute("error", "Comment cannot be empty.");
            return "redirect:/customer/reviews";
        }
        if (!ValidationUtils.isValidRating(rating)) {
            ra.addFlashAttribute("error", "Rating must be between 1 and 5.");
            return "redirect:/customer/reviews";
        }

        Review review = reviewService.submitReview(
                customer.getUserId(), bookingId, rating, comment.trim()
        );

        if (review == null) {
            ra.addFlashAttribute("error",
                    "Could not submit review. Booking must be completed and not already reviewed.");
            return "redirect:/customer/reviews";
        }

        ra.addFlashAttribute("success", "Thank you for your review!");
        return "redirect:/customer/reviews";
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        Customer customer = getCustomer(session);
        if (customer == null) return "redirect:/login";

        List<Payment> payments =
                paymentService.getPaymentsByCustomer(customer.getUserId());
        double totalSpent = 0;
        for (Payment p : payments) {
            if (Payment.STATUS_PAID.equals(p.getStatus())) totalSpent += p.getAmount();
        }

        model.addAttribute("customer",   customer);
        model.addAttribute("payments",   payments);
        model.addAttribute("totalSpent", totalSpent);
        return "customer/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(
            HttpSession session,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String password,
            @RequestParam(required = false) String phone,
            RedirectAttributes ra) {

        Customer customer = getCustomer(session);
        if (customer == null) return "redirect:/login";

        boolean updated = false;

        if (ValidationUtils.isNotEmpty(name)) {
            userService.updateName(customer, name.trim());
            updated = true;
        }
        if (ValidationUtils.isValidPassword(password)) {
            userService.updatePassword(customer, password);
            updated = true;
        }
        if (ValidationUtils.isValidPhone(phone)) {
            userService.updateCustomerPhone(customer, phone.trim());
            updated = true;
        }

        User refreshed = userService.findById(customer.getUserId());
        if (refreshed instanceof Customer) {
            SessionUtils.refreshUser(session, refreshed);
        }

        ra.addFlashAttribute(
                updated ? "success" : "error",
                updated ? "Profile updated successfully."
                        : "No valid changes detected."
        );
        return "redirect:/customer/profile";
    }
}