package com.vehiclerental.servlet;

import com.vehiclerental.model.User;
import com.vehiclerental.service.BookingService;
import com.vehiclerental.service.VehicleService;
import com.vehiclerental.util.JsonUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Endpoint for frontend JavaScript DOM scripts to check authentication status
 * and retrieve session user and dashboard counts.
 */
@WebServlet(name = "CurrentUserServlet", urlPatterns = {"/api/current-user", "/api/customer-stats"})
public class CurrentUserServlet extends HttpServlet {
    private final VehicleService vehicleService = new VehicleService();
    private final BookingService bookingService = new BookingService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String path = request.getServletPath();

        if ("/api/customer-stats".equalsIgnoreCase(path)) {
            if (user == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\":\"Not logged in\"}");
                return;
            }
            int availableVehicles = vehicleService.getAvailableVehicleCount();
            int activeBookings = bookingService.getActiveBookingCount(user.getId());
            int completedBookings = bookingService.getCompletedBookingCount(user.getId());

            response.getWriter().write("{"
                    + "\"availableVehicles\":" + availableVehicles + ","
                    + "\"activeBookings\":" + activeBookings + ","
                    + "\"completedBookings\":" + completedBookings
                    + "}");
            return;
        }

        if (user != null) {
            response.getWriter().write("{"
                    + "\"loggedIn\":true,"
                    + "\"id\":\"" + JsonUtil.escape(user.getId()) + "\","
                    + "\"name\":\"" + JsonUtil.escape(user.getName()) + "\","
                    + "\"username\":\"" + JsonUtil.escape(user.getUsername()) + "\","
                    + "\"role\":\"" + JsonUtil.escape(user.getRole()) + "\""
                    + "}");
        } else {
            response.getWriter().write("{\"loggedIn\":false}");
        }
    }
}
