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
import java.util.List;

/**
 * Servlet for retrieving available or all vehicles.
 */
@WebServlet(name = "VehicleServlet", urlPatterns = {"/vehicles"})
public class VehicleServlet extends HttpServlet {
    private final VehicleService vehicleService = new VehicleService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String filter = request.getParameter("filter");
        String format = request.getParameter("format");
        boolean isAjax = "json".equalsIgnoreCase(format)
                || "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"))
                || (request.getHeader("Accept") != null && request.getHeader("Accept").contains("application/json"));

        if (!isAjax) {
            response.sendRedirect(request.getContextPath() + "/vehicles.html");
            return;
        }

        List<Vehicle> vehicles;
        if ("available".equalsIgnoreCase(filter)) {
            vehicles = vehicleService.getAvailableVehicles();
        } else {
            vehicles = vehicleService.getAllVehicles();
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(JsonUtil.vehicleListToJson(vehicles));
    }
}
