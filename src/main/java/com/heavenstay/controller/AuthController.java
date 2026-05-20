package com.heavenstay.controller;

import com.heavenstay.models.Customer;
import com.heavenstay.models.User;
import com.heavenstay.service.EmailService;
import com.heavenstay.service.UserService;
import com.heavenstay.utils.SessionUtils;
import com.heavenstay.utils.ValidationUtils;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @GetMapping("/")
    public String root(HttpSession session) {
        if (SessionUtils.isLoggedIn(session)) {
            return SessionUtils.redirectByRole(session);
        }
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        if (SessionUtils.isLoggedIn(session)) {
            return SessionUtils.redirectByRole(session);
        }
        return "auth/login";
    }

    @PostMapping("/login")
    public String processLogin(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            RedirectAttributes ra) {

        if (!ValidationUtils.isNotEmpty(email) ||
                !ValidationUtils.isNotEmpty(password)) {
            ra.addFlashAttribute("error", "Email and password are required.");
            return "redirect:/login";
        }

        User user = userService.login(email.trim(), password.trim());

        if (user == null) {
            ra.addFlashAttribute("error", "Invalid email or password.");
            return "redirect:/login";
        }

        SessionUtils.setUser(session, user);
        ra.addFlashAttribute("success", "Welcome back, " + user.getName() + "!");
        return SessionUtils.redirectByRole(session);
    }

    @GetMapping("/register")
    public String registerPage(HttpSession session) {
        if (SessionUtils.isLoggedIn(session)) {
            return SessionUtils.redirectByRole(session);
        }
        return "auth/register";
    }

    @PostMapping("/register")
    public String processRegister(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String phone,
            RedirectAttributes ra) {

        if (!ValidationUtils.isNotEmpty(name)) {
            ra.addFlashAttribute("error", "Name is required.");
            return "redirect:/register";
        }

        if (!ValidationUtils.isValidEmail(email)) {
            ra.addFlashAttribute("error", "Enter a valid email address.");
            return "redirect:/register";
        }

        if (!ValidationUtils.isValidPassword(password)) {
            ra.addFlashAttribute("error", "Password must be at least 6 characters.");
            return "redirect:/register";
        }

        if (!ValidationUtils.isValidPhone(phone)) {
            ra.addFlashAttribute("error", "Phone must be 10 digits starting with 0.");
            return "redirect:/register";
        }

        Customer customer = userService.registerCustomer(
                name.trim(), email.trim(), password, phone.trim()
        );

        if (customer == null) {
            ra.addFlashAttribute("error", "Email already registered.");
            return "redirect:/register";
        }

        ra.addFlashAttribute("success",
                "Registration successful! Your ID: " + customer.getUserId() +
                        ". Please login.");
        return "redirect:/login";
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(
            @RequestParam String email,
            HttpSession session,
            RedirectAttributes ra) {

        User user = userService.findByEmail(email.trim());

        if (user == null) {
            ra.addFlashAttribute("error", "Email not found.");
            return "redirect:/forgot-password";
        }

        int otp = (int) (Math.random() * 900000) + 100000;

        session.setAttribute("resetEmail", email.trim());
        session.setAttribute("resetOtp", String.valueOf(otp));

        emailService.sendOtpEmail(email.trim(), String.valueOf(otp));

        ra.addFlashAttribute("success", "OTP sent to your email.");
        return "redirect:/reset-password";
    }

    @GetMapping("/reset-password")
    public String resetPasswordPage(HttpSession session, RedirectAttributes ra) {
        if (session.getAttribute("resetEmail") == null) {
            ra.addFlashAttribute("error", "Please enter your email first.");
            return "redirect:/forgot-password";
        }
        return "auth/reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(
            @RequestParam String otp,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            HttpSession session,
            RedirectAttributes ra) {

        String email = (String) session.getAttribute("resetEmail");
        String savedOtp = (String) session.getAttribute("resetOtp");

        if (email == null || savedOtp == null) {
            ra.addFlashAttribute("error", "Session expired. Try again.");
            return "redirect:/forgot-password";
        }

        if (!savedOtp.equals(otp.trim())) {
            ra.addFlashAttribute("error", "Invalid OTP.");
            return "redirect:/reset-password";
        }

        if (!password.equals(confirmPassword)) {
            ra.addFlashAttribute("error", "Passwords do not match.");
            return "redirect:/reset-password";
        }

        boolean updated = userService.resetPasswordByEmail(email, password);

        if (!updated) {
            ra.addFlashAttribute("error", "Password must be at least 6 characters.");
            return "redirect:/reset-password";
        }

        session.removeAttribute("resetEmail");
        session.removeAttribute("resetOtp");

        ra.addFlashAttribute("success", "Password changed successfully. Please login.");
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes ra) {
        String name = (String) session.getAttribute("userName");
        SessionUtils.invalidate(session);
        ra.addFlashAttribute("success",
                "Goodbye" + (name != null ? ", " + name : "") + "! See you soon.");
        return "redirect:/login";
    }
}