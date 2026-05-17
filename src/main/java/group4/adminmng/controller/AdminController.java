package group4.adminmng.controller;

// ================================================================
// File    : AdminController.java
// Package : com.heavenstay.controller
// Desc    : Spring MVC controller. Maps HTTP requests to the
//           three Admin Management pages.  Session-based auth.
// ================================================================

import group4.adminmng.model.*;
import group4.adminmng.service.AdminService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) { this.adminService = adminService; }

    // ──────────────────────────────────────────────────────────
    // LOGIN / LOGOUT
    // ──────────────────────────────────────────────────────────
    @GetMapping("/")
    public String root(HttpSession s) {
        return s.getAttribute("admin") != null ? "redirect:/admin/dashboard" : "redirect:/admin/login";
    }

    @GetMapping("/admin/login")
    public String loginPage(@RequestParam(required=false) String error,
                            @RequestParam(required=false) String logout, Model m) {
        if ("true".equals(error))  m.addAttribute("errorMsg", "Invalid credentials or account suspended.");
        if ("true".equals(logout)) m.addAttribute("logoutMsg", "You have been signed out.");
        return "admin-login";
    }

    @PostMapping("/admin/login")
    public String loginPost(@RequestParam String username, @RequestParam String password,
                            HttpSession session, RedirectAttributes ra) {
        AdminUser a = adminService.login(username, password);
        if (a == null) {
            ra.addFlashAttribute("errorMsg", "Invalid credentials or account suspended.");
            return "redirect:/admin/login";
        }
        session.setAttribute("admin", a);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/admin/logout")
    public String logout(HttpSession s) {
        s.invalidate();
        return "redirect:/admin/login?logout=true";
    }

    // ──────────────────────────────────────────────────────────
    // PAGE 1 — ADMIN DASHBOARD
    // ──────────────────────────────────────────────────────────
    @GetMapping("/admin/dashboard")
    public String dashboard(HttpSession session, Model m) {
        AdminUser admin = (AdminUser) session.getAttribute("admin");
        if (admin == null) return "redirect:/admin/login";

        // KPI stats
        OccupancyReport occ = adminService.generateOccupancyReport(
                admin.getUserId(), "All time");
        RevenueReport   rev = adminService.generateRevenueReport(
                admin.getUserId(), "All time");

        m.addAttribute("admin",          admin);
        m.addAttribute("totalAdmins",    adminService.countActiveAdmins());
        m.addAttribute("totalRooms",     occ.getTotalRooms());
        m.addAttribute("occupiedRooms",  occ.getOccupiedRooms());
        m.addAttribute("occupancyRate",  String.format("%.1f", occ.getOccupancyRate()));
        m.addAttribute("totalRevenue",   String.format("%.2f", rev.getTotalRevenue()));
        m.addAttribute("totalBookings",  rev.getTotalBookings());
        m.addAttribute("recentLogs",     adminService.getAdminLogs(null)
                .stream().limit(8).toList());
        m.addAttribute("config",         adminService.getConfig());
        return "admin-dashboard";
    }

    // ──────────────────────────────────────────────────────────
    // PAGE 2 — ADMIN REGISTRATION & MANAGEMENT
    // ──────────────────────────────────────────────────────────
    @GetMapping("/admin/manage")
    public String managePage(HttpSession session, Model m) {
        if (!isLoggedIn(session)) return "redirect:/admin/login";
        m.addAttribute("admin",  session.getAttribute("admin"));
        m.addAttribute("admins", adminService.getAllAdmins());
        return "admin-manage";
    }

    @PostMapping("/admin/manage/add")
    public String addAdmin(@RequestParam String username,
                           @RequestParam String password,
                           @RequestParam String confirmPassword,
                           @RequestParam String email,
                           @RequestParam(required=false) String contactNo,
                           @RequestParam String department,
                           @RequestParam(required=false, defaultValue="Morning") String shift,
                           @RequestParam(required=false, defaultValue="") String permissions,
                           HttpSession session, RedirectAttributes ra, Model m) {
        if (!isLoggedIn(session)) return "redirect:/admin/login";
        try {
            adminService.addAdmin(username, password, confirmPassword,
                    email, contactNo, department, shift, permissions);
            ra.addFlashAttribute("successMsg", "Admin '" + username + "' created.");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/admin/manage";
    }

    @PostMapping("/admin/manage/update")
    public String updateAdmin(@RequestParam String adminId,
                              @RequestParam String email,
                              @RequestParam(required=false) String contactNo,
                              @RequestParam String department,
                              @RequestParam(required=false, defaultValue="Morning") String shift,
                              @RequestParam(required=false) String newPassword,
                              @RequestParam(required=false) String confirmPassword,
                              @RequestParam(required=false, defaultValue="") String permissions,
                              HttpSession session, RedirectAttributes ra) {
        if (!isLoggedIn(session)) return "redirect:/admin/login";
        try {
            adminService.updateAdmin(adminId, email, contactNo, department, shift,
                    newPassword, confirmPassword, permissions);
            ra.addFlashAttribute("successMsg", "Admin updated.");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/admin/manage";
    }

    @PostMapping("/admin/manage/suspend")
    public String suspendAdmin(@RequestParam String adminId,
                               HttpSession session, RedirectAttributes ra) {
        if (!isLoggedIn(session)) return "redirect:/admin/login";
        try { adminService.suspendAdmin(adminId); ra.addFlashAttribute("successMsg","Admin suspended."); }
        catch (IllegalArgumentException e) { ra.addFlashAttribute("errorMsg", e.getMessage()); }
        return "redirect:/admin/manage";
    }

    @PostMapping("/admin/manage/restore")
    public String restoreAdmin(@RequestParam String adminId,
                               HttpSession session, RedirectAttributes ra) {
        if (!isLoggedIn(session)) return "redirect:/admin/login";
        try { adminService.restoreAdmin(adminId); ra.addFlashAttribute("successMsg","Admin restored."); }
        catch (IllegalArgumentException e) { ra.addFlashAttribute("errorMsg", e.getMessage()); }
        return "redirect:/admin/manage";
    }

    @PostMapping("/admin/manage/delete")
    public String deleteAdmin(@RequestParam String adminId,
                              HttpSession session, RedirectAttributes ra) {
        if (!isLoggedIn(session)) return "redirect:/admin/login";
        try { adminService.deleteAdmin(adminId); ra.addFlashAttribute("successMsg","Admin deleted."); }
        catch (IllegalArgumentException e) { ra.addFlashAttribute("errorMsg", e.getMessage()); }
        return "redirect:/admin/manage";
    }

    // ──────────────────────────────────────────────────────────
    // PAGE 3 — REPORTS & CONFIGURATION
    // ──────────────────────────────────────────────────────────
    @GetMapping("/admin/reports")
    public String reportsPage(HttpSession session, Model m) {
        if (!isLoggedIn(session)) return "redirect:/admin/login";
        AdminUser admin = (AdminUser) session.getAttribute("admin");
        m.addAttribute("admin",  admin);
        m.addAttribute("config", adminService.getConfig());
        m.addAttribute("logs",   adminService.getAdminLogs(null).stream().limit(20).toList());
        return "admin-reports";
    }

    @PostMapping("/admin/reports/occupancy")
    public String occupancyReport(@RequestParam(required=false, defaultValue="All time") String dateRange,
                                  HttpSession session, Model m) {
        if (!isLoggedIn(session)) return "redirect:/admin/login";
        AdminUser admin = (AdminUser) session.getAttribute("admin");
        OccupancyReport r = adminService.generateOccupancyReport(admin.getUserId(), dateRange);
        adminService.logActivity(admin.getUserId(), admin.getUsername(), "REPORT_OCCUPANCY_GENERATED");
        m.addAttribute("admin",   admin);
        m.addAttribute("config",  adminService.getConfig());
        m.addAttribute("occReport", r);
        m.addAttribute("logs",    adminService.getAdminLogs(null).stream().limit(20).toList());
        return "admin-reports";
    }

    @PostMapping("/admin/reports/revenue")
    public String revenueReport(@RequestParam(required=false, defaultValue="All time") String dateRange,
                                HttpSession session, Model m) {
        if (!isLoggedIn(session)) return "redirect:/admin/login";
        AdminUser admin = (AdminUser) session.getAttribute("admin");
        RevenueReport r = adminService.generateRevenueReport(admin.getUserId(), dateRange);
        adminService.logActivity(admin.getUserId(), admin.getUsername(), "REPORT_REVENUE_GENERATED");
        m.addAttribute("admin",   admin);
        m.addAttribute("config",  adminService.getConfig());
        m.addAttribute("revReport", r);
        m.addAttribute("logs",    adminService.getAdminLogs(null).stream().limit(20).toList());
        return "admin-reports";
    }

    @PostMapping("/admin/config/save")
    public String saveConfig(@RequestParam String hotelName,
                             @RequestParam String checkInTime,
                             @RequestParam String checkOutTime,
                             @RequestParam double taxRate,
                             @RequestParam String cancellationPolicy,
                             @RequestParam String currency,
                             @RequestParam int    maxFloors,
                             HttpSession session, RedirectAttributes ra) {
        if (!isLoggedIn(session)) return "redirect:/admin/login";
        AdminUser admin = (AdminUser) session.getAttribute("admin");
        try {
            HotelConfig cfg = new HotelConfig(hotelName, checkInTime, checkOutTime,
                    taxRate, cancellationPolicy, currency, maxFloors);
            adminService.saveConfig(cfg);
            adminService.logActivity(admin.getUserId(), admin.getUsername(), "CONFIG_UPDATED");
            ra.addFlashAttribute("successMsg", "Hotel configuration saved.");
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/admin/reports";
    }

    // ── Helper ────────────────────────────────────────────────
    private boolean isLoggedIn(HttpSession s) {
        return s.getAttribute("admin") != null;
    }
}
