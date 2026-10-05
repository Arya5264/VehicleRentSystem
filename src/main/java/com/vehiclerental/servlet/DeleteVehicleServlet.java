package com.vehiclerental.servlet;

import com.vehiclerental.model.User;
import com.vehiclerental.service.VehicleService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Servlet handling vehicle removal by administrators.
 */
@WebServlet(name = "DeleteVehicleServlet", urlPatterns = {"/admin/delete-vehicle"})
public class DeleteVehicleServlet extends HttpServlet {
    private final VehicleService vehicleService = new VehicleService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        handleDelete(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        handleDelete(request, response);
    }

    private void handleDelete(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
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

        String id = request.getParameter("id");
        boolean success = vehicleService.deleteVehicle(id);

        if (isAjax) {
            response.setContentType("application/json");
            response.getWriter().write("{\"success\":" + success + "}");
        } else {
            response.sendRedirect(request.getContextPath() + "/manage-vehicles.html?deleted=" + success);
        }
    }
}
