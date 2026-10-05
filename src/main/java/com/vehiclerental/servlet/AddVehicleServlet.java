package com.vehiclerental.servlet;

import com.vehiclerental.model.User;
import com.vehiclerental.model.Vehicle;
import com.vehiclerental.service.VehicleService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Servlet allowing administrators to add new vehicles to vehicles.xml.
 */
@WebServlet(name = "AddVehicleServlet", urlPatterns = {"/admin/add-vehicle"})
public class AddVehicleServlet extends HttpServlet {
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
        response.sendRedirect(request.getContextPath() + "/add-vehicle.html");
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

        Vehicle vehicle = new Vehicle(id, name, brand, model, type, fuel, price, regNum, 
                status != null ? status : "Available");
        boolean success = vehicleService.addVehicle(vehicle);

        if (isAjax) {
            response.setContentType("application/json");
            response.getWriter().write("{\"success\":" + success + "}");
        } else {
            response.sendRedirect(request.getContextPath() + "/manage-vehicles.html?added=" + success);
        }
    }
}
