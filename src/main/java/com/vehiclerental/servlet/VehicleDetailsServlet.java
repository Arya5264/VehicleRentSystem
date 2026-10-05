package com.vehiclerental.servlet;

import com.vehiclerental.model.Vehicle;
import com.vehiclerental.service.VehicleService;
import com.vehiclerental.util.JsonUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Servlet for retrieving details for a single vehicle by ID.
 */
@WebServlet(name = "VehicleDetailsServlet", urlPatterns = {"/vehicle-details"})
public class VehicleDetailsServlet extends HttpServlet {
    private final VehicleService vehicleService = new VehicleService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String id = request.getParameter("id");
        String format = request.getParameter("format");

        if (id == null || id.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/vehicles.html");
            return;
        }

        Vehicle vehicle = vehicleService.getVehicleById(id.trim());

        boolean isAjax = "json".equalsIgnoreCase(format)
                || "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"))
                || (request.getHeader("Accept") != null && request.getHeader("Accept").contains("application/json"));

        if (isAjax) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            if (vehicle != null) {
                response.getWriter().write(JsonUtil.toJson(vehicle));
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\":\"Vehicle not found\"}");
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/vehicle-details.html?id=" + id.trim());
        }
    }
}
