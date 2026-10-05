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
import java.util.List;

/**
 * Servlet for retrieving rental bookings associated with the currently logged-in user.
 */
@WebServlet(name = "MyBookingsServlet", urlPatterns = {"/my-bookings"})
public class MyBookingsServlet extends HttpServlet {
    private final BookingService bookingService = new BookingService();

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
                response.getWriter().write("{\"error\":\"User not logged in\"}");
            } else {
                response.sendRedirect(request.getContextPath() + "/login.html");
            }
            return;
        }

        if (!isAjax) {
            response.sendRedirect(request.getContextPath() + "/my-bookings.html");
            return;
        }

        List<Booking> list = bookingService.getBookingsByUserId(user.getId());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(JsonUtil.bookingListToJson(list));
    }
}
