package com.vehiclerental.servlet;

import com.vehiclerental.model.Booking;
import com.vehiclerental.model.User;
import com.vehiclerental.service.BookingService;
import com.vehiclerental.util.JsonUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Servlet handling vehicle booking creation and rental amount calculation.
 */
@WebServlet(name = "BookingServlet", urlPatterns = {"/book"})
public class BookingServlet extends HttpServlet {
    private final BookingService bookingService = new BookingService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String vehicleId = request.getParameter("vehicleId");
        if (vehicleId != null) {
            response.sendRedirect(request.getContextPath() + "/booking.html?vehicleId=" + vehicleId);
        } else {
            response.sendRedirect(request.getContextPath() + "/vehicles.html");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        boolean isAjax = "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"))
                || "application/json".equalsIgnoreCase(request.getHeader("Accept"));

        if (user == null) {
            if (isAjax) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"success\":false,\"message\":\"Please login to rent a vehicle.\"}");
            } else {
                response.sendRedirect(request.getContextPath() + "/login.html?error=Please+login+to+rent+a+vehicle");
            }
            return;
        }

        String vehicleId = request.getParameter("vehicleId");
        String pickupDate = request.getParameter("pickupDate");
        String returnDate = request.getParameter("returnDate");

        try {
            Booking booking = bookingService.createBooking(user.getId(), user.getName(), vehicleId, pickupDate, returnDate);

            if (isAjax) {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("{\"success\":true,\"booking\":" + JsonUtil.toJson(booking) + "}");
            } else {
                response.sendRedirect(request.getContextPath() + "/booking.html?success=true&bookingId=" 
                        + booking.getBookingId() + "&vehicleId=" + vehicleId);
            }
        } catch (Exception e) {
            if (isAjax) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("{\"success\":false,\"message\":\"" + JsonUtil.escape(e.getMessage()) + "\"}");
            } else {
                String errorMsg = URLEncoder.encode(e.getMessage() != null ? e.getMessage() : "Booking failed", StandardCharsets.UTF_8);
                response.sendRedirect(request.getContextPath() + "/booking.html?error=" + errorMsg + "&vehicleId=" + (vehicleId != null ? vehicleId : ""));
            }
        }
    }
}
