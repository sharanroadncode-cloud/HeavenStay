package com.heavenstay.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class SessionInterceptor implements HandlerInterceptor {

    private static final String[] PUBLIC_URLS = {
            "/login", "/register", "/logout",
            "/forgot-password", "/reset-password",
            "/css/", "/js/", "/images/", "/error"
    };

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        String uri = request.getRequestURI();

        for (String pub : PUBLIC_URLS) {
            if (uri.startsWith(pub)) return true;
        }

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loggedInUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }

        String role = (String) session.getAttribute("userRole");
        if (role == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }

        if (uri.startsWith("/admin") && !"ADMIN".equals(role)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }

        if (uri.startsWith("/reception") && !"RECEPTIONIST".equals(role)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }

        if (uri.startsWith("/customer") && !"CUSTOMER".equals(role)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }

        return true;
    }
}