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
 * Servlet allowing administrators to view, approve, cancel, or complete customer bookings.
 */
@WebServlet(name = "ManageBookingsServlet", urlPatterns = {"/admin/bookings"})
public class ManageBookingsServlet extends HttpServlet {
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

        if (user == null || !user.isAdmin()) {
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
            response.sendRedirect(request.getContextPath() + "/manage-bookings.html");
            return;
        }

        List<Booking> list = bookingService.getAllBookings();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(JsonUtil.bookingListToJson(list));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        boolean isAjax = "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"))
                || "application/json".equalsIgnoreCase(request.getHeader("Accept"));

        if (user == null || !user.isAdmin()) {
            if (isAjax) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                response.getWriter().write("{\"success\":false,\"message\":\"Admin access required.\"}");
            } else {
                response.sendRedirect(request.getContextPath() + "/access-denied.html");
            }
            return;
        }

        String bookingId = request.getParameter("bookingId");
        String action = request.getParameter("action");

        boolean success = false;
        if ("approve".equalsIgnoreCase(action)) {
            success = bookingService.approveBooking(bookingId);
        } else if ("cancel".equalsIgnoreCase(action)) {
            success = bookingService.cancelBooking(bookingId, user.getId(), true);
        } else if ("complete".equalsIgnoreCase(action)) {
            success = bookingService.completeBooking(bookingId);
        }

        if (isAjax) {
            response.setContentType("application/json");
            response.getWriter().write("{\"success\":" + success + ",\"action\":\"" + action + "\"}");
        } else {
            response.sendRedirect(request.getContextPath() + "/manage-bookings.html?statusUpdated=" + success);
        }
    }
}
