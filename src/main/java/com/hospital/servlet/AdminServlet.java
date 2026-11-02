package com.hospital.servlet;

import com.hospital.dao.*;
import com.hospital.model.Doctor;
import com.hospital.service.AccountService;
import com.hospital.util.Flash;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/admin")
public class AdminServlet extends HttpServlet {
    private final DoctorDAO doctors = new DoctorDAO();
    private final AppointmentDAO appointments = new AppointmentDAO();
    private final ReportDAO reports = new ReportDAO();
    private final UserDAO users = new UserDAO();
    private final AccountService accounts = new AccountService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Flash.load(req);
        req.setAttribute("doctors", doctors.findAll());               // Hibernate
        req.setAttribute("appointments", appointments.findAll());     // Hibernate
        req.setAttribute("totals", reports.totals());                 // JDBC
        req.setAttribute("statusCounts", reports.statusCounts());     // JDBC
        req.setAttribute("perDoctor", reports.appointmentsPerDoctor());// JDBC
        req.getRequestDispatcher("/WEB-INF/views/admin.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String action = req.getParameter("action");
        try {
            if ("addDoctor".equals(action)) {
                accounts.registerDoctor(val(req, "username"), req.getParameter("password"),
                        val(req, "fullName"), val(req, "specialization"), Double.parseDouble(val(req, "fee")));
                Flash.set(req, "success", "Doctor added. They can sign in with the username and password you set.");
            } else if ("deleteDoctor".equals(action)) {
                long id = Long.parseLong(val(req, "id"));
                Doctor d = doctors.findById(id);
                if (d != null) {
                    doctors.delete(id);
                    users.delete(d.getUserId());
                    Flash.set(req, "success", d.getName() + " and their appointments were removed.");
                }
            }
        } catch (NumberFormatException e) {
            Flash.set(req, "error", "Enter the fee as a number.");
        } catch (IllegalArgumentException e) {
            Flash.set(req, "error", e.getMessage());
        } catch (RuntimeException e) {
            Flash.set(req, "error", "Action failed: " + e.getMessage());
        }
        resp.sendRedirect(req.getContextPath() + "/admin");
    }

    private static String val(HttpServletRequest req, String name) {
        String v = req.getParameter(name);
        return v == null ? "" : v.trim();
    }
}
