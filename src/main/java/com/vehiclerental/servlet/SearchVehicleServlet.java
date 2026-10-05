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
 * Servlet for searching and filtering vehicles using XPath parameters.
 */
@WebServlet(name = "SearchVehicleServlet", urlPatterns = {"/search-vehicles"})
public class SearchVehicleServlet extends HttpServlet {
    private final VehicleService vehicleService = new VehicleService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String keyword = request.getParameter("keyword");
        String type = request.getParameter("type");
        String fuel = request.getParameter("fuel");
        String status = request.getParameter("status");
        String maxPriceStr = request.getParameter("maxPrice");

        Double maxPrice = null;
        if (maxPriceStr != null && !maxPriceStr.trim().isEmpty()) {
            try {
                maxPrice = Double.parseDouble(maxPriceStr.trim());
            } catch (NumberFormatException ignored) {
            }
        }

        List<Vehicle> list = vehicleService.searchVehicles(keyword, type, fuel, maxPrice, status);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(JsonUtil.vehicleListToJson(list));
    }
}
