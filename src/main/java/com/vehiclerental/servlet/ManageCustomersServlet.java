package com.vehiclerental.servlet;

import com.vehiclerental.model.User;
import com.vehiclerental.service.UserService;
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
 * Servlet allowing administrators to view all registered customers.
 */
@WebServlet(name = "ManageCustomersServlet", urlPatterns = {"/admin/customers"})
public class ManageCustomersServlet extends HttpServlet {
    private final UserService userService = new UserService();

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
            response.sendRedirect(request.getContextPath() + "/customers.html");
            return;
        }

        List<User> list = userService.getAllCustomers();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(JsonUtil.userListToJson(list));
    }
}
