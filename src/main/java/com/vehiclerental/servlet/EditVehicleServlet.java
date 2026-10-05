package com.vehiclerental.servlet;

import com.vehiclerental.model.User;
import com.vehiclerental.model.Vehicle;
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
 * Servlet allowing administrators to fetch and update vehicle details.
 */
@WebServlet(name = "EditVehicleServlet", urlPatterns = {"/admin/edit-vehicle"})
public class EditVehicleServlet extends HttpServlet {
    private final VehicleService vehicleService = new VehicleService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        if (user == null || !user.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/access-denied.html");
            return;
        }

        String id = request.getParameter("id");
        String format = request.getParameter("format");

        if (id == null || id.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/manage-vehicles.html");
            return;
        }

        Vehicle vehicle = vehicleService.getVehicleById(id.trim());

        if ("json".equalsIgnoreCase(format) || 
            "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"))) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            if (vehicle != null) {
                response.getWriter().write(JsonUtil.toJson(vehicle));
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\":\"Vehicle not found\"}");
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/edit-vehicle.html?id=" + id.trim());
        }
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

        String id = request.getParameter("id");
        String name = request.getParameter("name");
        String brand = request.getParameter("brand");
        String model = request.getParameter("model");
        String type = request.getParameter("type");
        String fuel = request.getParameter("fuel");
        String priceStr = request.getParameter("pricePerDay");
        String regNum = request.getParameter("registrationNumber");
        String status = request.getParameter("status");

        double price = 0.0;
        try {
            if (priceStr != null) price = Double.parseDouble(priceStr);
        } catch (NumberFormatException ignored) {
        }

        Vehicle vehicle = new Vehicle(id, name, brand, model, type, fuel, price, regNum, status);
        boolean success = vehicleService.updateVehicle(vehicle);

        if (isAjax) {
            response.setContentType("application/json");
            response.getWriter().write("{\"success\":" + success + "}");
        } else {
            response.sendRedirect(request.getContextPath() + "/manage-vehicles.html?updated=" + success);
        }
    }
}
