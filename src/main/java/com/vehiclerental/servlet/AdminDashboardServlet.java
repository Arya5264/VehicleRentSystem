package com.vehiclerental.servlet;

import com.vehiclerental.model.User;
import com.vehiclerental.service.BookingService;
import com.vehiclerental.service.UserService;
import com.vehiclerental.service.VehicleService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Servlet providing administrative metrics and protecting admin routes.
 */
@WebServlet(name = "AdminDashboardServlet", urlPatterns = {"/admin/dashboard"})
public class AdminDashboardServlet extends HttpServlet {
    private final VehicleService vehicleService = new VehicleService();
    private final BookingService bookingService = new BookingService();
    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        String format = request.getParameter("format");
        boolean isAjax = "json".equalsIgnoreCase(format)
                || "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"))
                || (request.getHeader("Accept") != null && request.getHeader("Accept").contains("application/json"));

        if (user == null) {
            if (isAjax) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Unauthorized. Please login.\"}");
            } else {
                response.sendRedirect(request.getContextPath() + "/login.html");
            }
            return;
        }

        if (!user.isAdmin()) {
            if (isAjax) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Forbidden: Admin access required.\"}");
            } else {
                response.sendRedirect(request.getContextPath() + "/access-denied.html");
            }
            return;
        }

        if (!isAjax) {
            response.sendRedirect(request.getContextPath() + "/admin-dashboard.html");
            return;
        }

        int totalVehicles = vehicleService.getTotalVehicleCount();
        int availableVehicles = vehicleService.getAvailableVehicleCount();
        int rentedVehicles = vehicleService.getRentedVehicleCount();
        int totalBookings = bookingService.getTotalBookingCount();
        int totalCustomers = userService.getCustomerCount();

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{"
                + "\"totalVehicles\":" + totalVehicles + ","
                + "\"availableVehicles\":" + availableVehicles + ","
                + "\"rentedVehicles\":" + rentedVehicles + ","
                + "\"totalBookings\":" + totalBookings + ","
                + "\"totalCustomers\":" + totalCustomers
                + "}");
    }
}
