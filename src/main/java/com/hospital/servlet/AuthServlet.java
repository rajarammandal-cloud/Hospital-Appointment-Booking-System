package com.hospital.servlet;

import com.hospital.dao.UserDAO;
import com.hospital.model.User;
import com.hospital.service.AccountService;
import com.hospital.util.Flash;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet({"/login", "/logout", "/register"})
public class AuthServlet extends HttpServlet {
    private final UserDAO users = new UserDAO();
    private final AccountService accounts = new AccountService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        switch (req.getServletPath()) {
            case "/logout" -> {
                HttpSession s = req.getSession(false);
                if (s != null) s.invalidate();
                resp.sendRedirect(req.getContextPath() + "/login");
            }
            case "/register" -> {
                Flash.load(req);
                req.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(req, resp);
            }
            default -> {
                HttpSession s = req.getSession(false);
                User u = s == null ? null : (User) s.getAttribute("user");
                if (u != null) { resp.sendRedirect(home(req, u)); return; }
                Flash.load(req);
                req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if ("/register".equals(req.getServletPath())) register(req, resp); else login(req, resp);
    }

    private void login(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String username = trim(req.getParameter("username"));
        String password = req.getParameter("password");
        try {
            User u = (username.isEmpty() || password == null) ? null : users.authenticate(username, password);
            if (u == null) {
                Flash.set(req, "error", "Wrong username or password. Check both and try again.");
                resp.sendRedirect(req.getContextPath() + "/login");
                return;
            }
            u.setPasswordHash(null);
            HttpSession old = req.getSession(false);
            if (old != null) old.invalidate();          // new session id after login
            req.getSession(true).setAttribute("user", u);
            resp.sendRedirect(home(req, u));
        } catch (RuntimeException e) {
            Flash.set(req, "error", "Could not reach the database. Start MySQL in XAMPP and try again.");
            resp.sendRedirect(req.getContextPath() + "/login");
        }
    }

    private void register(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            accounts.registerPatient(trim(req.getParameter("username")), req.getParameter("password"),
                    trim(req.getParameter("fullName")), Integer.parseInt(trim(req.getParameter("age"))),
                    req.getParameter("gender"), trim(req.getParameter("phone")));
            Flash.set(req, "success", "Account created. Sign in to book your first appointment.");
            resp.sendRedirect(req.getContextPath() + "/login");
        } catch (NumberFormatException e) {
            Flash.set(req, "error", "Enter your age as a number.");
            resp.sendRedirect(req.getContextPath() + "/register");
        } catch (IllegalArgumentException e) {
            Flash.set(req, "error", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/register");
        } catch (RuntimeException e) {
            Flash.set(req, "error", "Registration failed: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/register");
        }
    }

    private static String home(HttpServletRequest req, User u) {
        String path = switch (u.getRole()) {
            case User.ADMIN -> "/admin";
            case User.DOCTOR -> "/doctor";
            default -> "/patient";
        };
        return req.getContextPath() + path;
    }

    private static String trim(String s) { return s == null ? "" : s.trim(); }
}
