package com.heavenstay.utils;

import com.heavenstay.models.Customer;
import com.heavenstay.models.User;
import jakarta.servlet.http.HttpSession;

public class SessionUtils {

    private static final String SESSION_USER = "loggedInUser";
    private static final String SESSION_NAME = "userName";
    private static final String SESSION_ROLE = "userRole";

    public static void setUser(HttpSession session, User user) {
        if (session == null || user == null) return;
        session.setAttribute(SESSION_USER, user);
        session.setAttribute(SESSION_NAME, user.getName());
        session.setAttribute(SESSION_ROLE, user.getRole());
    }

    public static User getUser(HttpSession session) {
        if (session == null) return null;
        return (User) session.getAttribute(SESSION_USER);
    }

    public static boolean isLoggedIn(HttpSession session) {
        return getUser(session) != null;
    }

    public static boolean isAdmin(HttpSession session) {
        User user = getUser(session);
        return user != null && "ADMIN".equals(user.getRole());
    }

    public static boolean isReceptionist(HttpSession session) {
        User user = getUser(session);
        return user != null && "RECEPTIONIST".equals(user.getRole());
    }

    public static boolean isCustomer(HttpSession session) {
        User user = getUser(session);
        return user != null && "CUSTOMER".equals(user.getRole());
    }

    public static boolean isStaff(HttpSession session) {
        User user = getUser(session);
        return user != null && (
                "ADMIN".equals(user.getRole()) ||
                        "RECEPTIONIST".equals(user.getRole())
        );
    }

    public static Customer getCustomer(HttpSession session) {
        User user = getUser(session);
        if (user == null || !"CUSTOMER".equals(user.getRole())) return null;
        return (Customer) user;
    }

    public static void refreshUser(HttpSession session, User updatedUser) {
        if (session == null || updatedUser == null) return;
        session.setAttribute(SESSION_USER, updatedUser);
        session.setAttribute(SESSION_NAME, updatedUser.getName());
        session.setAttribute(SESSION_ROLE, updatedUser.getRole());
    }

    public static void invalidate(HttpSession session) {
        if (session != null) session.invalidate();
    }

    public static String redirectByRole(HttpSession session) {
        User user = getUser(session);
        if (user == null) return "redirect:/login";
        switch (user.getRole()) {
            case "ADMIN":        return "redirect:/admin/dashboard";
            case "RECEPTIONIST": return "redirect:/reception/dashboard";
            case "CUSTOMER":     return "redirect:/customer/dashboard";
            default:             return "redirect:/login";
        }
    }
}