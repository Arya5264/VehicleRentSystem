package com.vehiclerental.servlet;

import com.vehiclerental.model.User;
import com.vehiclerental.service.BookingService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Servlet handling booking cancellations by customers or admin.
 */
@WebServlet(name = "CancelBookingServlet", urlPatterns = {"/cancel-booking"})
public class CancelBookingServlet extends HttpServlet {
    private final BookingService bookingService = new BookingService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doCancel(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doCancel(request, response);
    }

    private void doCancel(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        boolean isAjax = "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"))
                || "application/json".equalsIgnoreCase(request.getHeader("Accept"));

        if (user == null) {
            if (isAjax) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"success\":false,\"message\":\"Unauthorized\"}");
            } else {
                response.sendRedirect(request.getContextPath() + "/login.html");
            }
            return;
        }

        String bookingId = request.getParameter("bookingId");
        boolean success = bookingService.cancelBooking(bookingId, user.getId(), user.isAdmin());

        if (isAjax) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"success\":" + success + "}");
        } else {
            String redirectUrl = user.isAdmin()
                    ? request.getContextPath() + "/admin-dashboard.html"
                    : request.getContextPath() + "/my-bookings.html";
            response.sendRedirect(redirectUrl + "?cancelled=" + success);
        }
    }
}
