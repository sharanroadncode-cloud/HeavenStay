package com.heavenstay.controller;

import com.heavenstay.models.Booking;
import com.heavenstay.models.User;
import com.heavenstay.service.AdminService;
import com.heavenstay.service.BookingService;
import com.heavenstay.service.PaymentService;
import com.heavenstay.service.ReviewService;
import com.heavenstay.service.RoomService;
import com.heavenstay.service.UserService;
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

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private AdminService   adminService;
    @Autowired private UserService    userService;
    @Autowired private BookingService bookingService;
    @Autowired private PaymentService paymentService;
    @Autowired private RoomService    roomService;
    @Autowired private ReviewService  reviewService;

    private User getAdmin(HttpSession session) {
        if (!SessionUtils.isAdmin(session)) return null;
        return SessionUtils.getUser(session);
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User admin = getAdmin(session);
        if (admin == null) return "redirect:/login";

        Map<String, Object> stats = adminService.getSystemStats();
        List<Booking> all = bookingService.getAllBookings();
        List<Booking> recent = all.size() > 5
                ? all.subList(all.size() - 5, all.size())
                : all;

        model.addAttribute("admin",          admin);
        model.addAttribute("recentBookings", recent);
        model.addAllAttributes(stats);
        return "admin/dashboard";
    }

    @GetMapping("/staff")
    public String staffList(HttpSession session, Model model) {
        User admin = getAdmin(session);
        if (admin == null) return "redirect:/login";

        model.addAttribute("admin",     admin);
        model.addAttribute("staff",     userService.getAllStaff());
        model.addAttribute("customers", userService.getAllCustomers());
        return "admin/staff";
    }

    @GetMapping("/staff/add")
    public String addStaffForm(
            HttpSession session,
            @RequestParam(required = false) String role,
            Model model) {

        User admin = getAdmin(session);
        if (admin == null) return "redirect:/login";

        model.addAttribute("admin",    admin);
        model.addAttribute("roleHint", role);
        return "admin/staff-add";
    }

    @PostMapping("/staff")
    public String addStaff(
            HttpSession session,
            @RequestParam String role,
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam(required = false) String shift,
            @RequestParam(required = false) String adminLevel,
            RedirectAttributes ra) {

        User admin = getAdmin(session);
        if (admin == null) return "redirect:/login";

        if (!ValidationUtils.isNotEmpty(name) ||
                !ValidationUtils.isValidEmail(email) ||
                !ValidationUtils.isValidPassword(password)) {
            ra.addFlashAttribute("error",
                    "Invalid input. Check name, email and password (min 6 chars).");
            return "redirect:/admin/staff/add";
        }

        boolean success = false;
        if ("RECEPTIONIST".equals(role)) {
            String s = ValidationUtils.isNotEmpty(shift)
                    ? shift.toUpperCase() : "MORNING";
            success = userService.addReceptionist(
                    name.trim(), email.trim(), password, s) != null;
        } else if ("ADMIN".equals(role)) {
            String lvl = ValidationUtils.isNotEmpty(adminLevel)
                    ? adminLevel.toUpperCase() : "REGULAR";
            success = userService.addAdmin(
                    name.trim(), email.trim(), password, lvl) != null;
        }

        ra.addFlashAttribute(
                success ? "success" : "error",
                success ? role + " account created for " + name + "."
                        : "Failed to create account. Email may already be in use."
        );
        return "redirect:/admin/staff";
    }

    @PostMapping("/staff/delete")
    public String deleteStaff(
            HttpSession session,
            @RequestParam String userId,
            RedirectAttributes ra) {

        User admin = getAdmin(session);
        if (admin == null) return "redirect:/login";

        if (admin.getUserId().equals(userId)) {
            ra.addFlashAttribute("error", "You cannot delete your own account.");
            return "redirect:/admin/staff";
        }

        User target = userService.findById(userId);
        if (target == null || "CUSTOMER".equals(target.getRole())) {
            ra.addFlashAttribute("error", "Staff member not found.");
            return "redirect:/admin/staff";
        }

        boolean deleted = userService.deleteUser(userId);
        ra.addFlashAttribute(
                deleted ? "success" : "error",
                deleted ? "Staff member deleted: " + userId
                        : "Could not delete: " + userId
        );
        return "redirect:/admin/staff";
    }

    @PostMapping("/customer/delete")
    public String deleteCustomer(
            HttpSession session,
            @RequestParam String userId,
            RedirectAttributes ra) {

        User admin = getAdmin(session);
        if (admin == null) return "redirect:/login";

        boolean deleted = userService.deleteUser(userId);
        ra.addFlashAttribute(
                deleted ? "success" : "error",
                deleted ? "Customer deleted: " + userId
                        : "Could not delete customer: " + userId
        );
        return "redirect:/admin/staff";
    }

    @GetMapping("/reports")
    public String systemReport(HttpSession session, Model model) {
        User admin = getAdmin(session);
        if (admin == null) return "redirect:/login";

        model.addAttribute("admin",    admin);
        model.addAttribute("rooms",    roomService.getAllRooms());
        model.addAttribute("bookings", bookingService.getAllBookings());
        model.addAllAttributes(adminService.getSystemStats());
        return "admin/reports";
    }

    @GetMapping("/revenue")
    public String revenueReport(HttpSession session, Model model) {
        User admin = getAdmin(session);
        if (admin == null) return "redirect:/login";

        model.addAttribute("admin",    admin);
        model.addAttribute("payments", paymentService.getAllPayments());
        model.addAllAttributes(adminService.getSystemStats());
        return "admin/revenue";
    }

    @GetMapping("/bookings")
    public String allBookings(
            HttpSession session,
            @RequestParam(required = false) String filter,
            Model model) {

        User admin = getAdmin(session);
        if (admin == null) return "redirect:/login";

        List<Booking> bookings = "active".equals(filter)
                ? bookingService.getActiveBookings()
                : bookingService.getAllBookings();

        model.addAttribute("admin",    admin);
        model.addAttribute("bookings", bookings);
        model.addAttribute("filter",   filter);
        return "admin/bookings";
    }

    @GetMapping("/reviews")
    public String allReviews(HttpSession session, Model model) {
        User admin = getAdmin(session);
        if (admin == null) return "redirect:/login";

        model.addAttribute("admin",   admin);
        model.addAttribute("reviews", reviewService.getAllReviews());
        return "admin/reviews";
    }

    @PostMapping("/reviews/delete")
    public String deleteReview(
            HttpSession session,
            @RequestParam String reviewId,
            RedirectAttributes ra) {

        User admin = getAdmin(session);
        if (admin == null) return "redirect:/login";

        boolean deleted = reviewService.deleteReview(reviewId);
        ra.addFlashAttribute(
                deleted ? "success" : "error",
                deleted ? "Review " + reviewId + " deleted."
                        : "Could not delete review: " + reviewId
        );
        return "redirect:/admin/reviews";
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        User admin = getAdmin(session);
        if (admin == null) return "redirect:/login";

        model.addAttribute("admin", admin);
        return "admin/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(
            HttpSession session,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String password,
            RedirectAttributes ra) {

        User admin = getAdmin(session);
        if (admin == null) return "redirect:/login";

        boolean updated = false;
        if (ValidationUtils.isNotEmpty(name)) {
            userService.updateName(admin, name.trim());
            updated = true;
        }
        if (ValidationUtils.isValidPassword(password)) {
            userService.updatePassword(admin, password);
            updated = true;
        }

        User refreshed = userService.findById(admin.getUserId());
        if (refreshed != null) SessionUtils.refreshUser(session, refreshed);

        ra.addFlashAttribute(
                updated ? "success" : "error",
                updated ? "Profile updated." : "No valid changes."
        );
        return "redirect:/admin/profile";
    }
}