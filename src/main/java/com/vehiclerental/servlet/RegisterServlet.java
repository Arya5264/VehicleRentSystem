package com.vehiclerental.servlet;

import com.vehiclerental.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Servlet handling customer registration into users.xml.
 */
@WebServlet(name = "RegisterServlet", urlPatterns = {"/register"})
public class RegisterServlet extends HttpServlet {
    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/register.html");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String name = request.getParameter("name");
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        boolean isAjax = "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"))
                || "application/json".equalsIgnoreCase(request.getHeader("Accept"));

        if (name == null || username == null || password == null ||
                name.trim().isEmpty() || username.trim().isEmpty() || password.trim().isEmpty()) {
            if (isAjax) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("application/json");
                response.getWriter().write("{\"success\":false,\"message\":\"All fields are required.\"}");
            } else {
                response.sendRedirect(request.getContextPath() + "/register.html?error=All+fields+are+required");
            }
            return;
        }

        boolean registered = userService.registerCustomer(name.trim(), username.trim(), password.trim());

        if (registered) {
            if (isAjax) {
                response.setContentType("application/json");
                response.getWriter().write("{\"success\":true,\"message\":\"Account registered successfully.\"}");
            } else {
                response.sendRedirect(request.getContextPath() + "/login.html?registered=true");
            }
        } else {
            if (isAjax) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                response.setContentType("application/json");
                response.getWriter().write("{\"success\":false,\"message\":\"Username is already registered.\"}");
            } else {
                response.sendRedirect(request.getContextPath() + "/register.html?error=Username+is+already+registered");
            }
        }
    }
}
