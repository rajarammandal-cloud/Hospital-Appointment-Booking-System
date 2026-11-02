package com.hospital.servlet;

import com.hospital.dao.*;
import com.hospital.model.*;
import com.hospital.util.Flash;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.Set;

@WebServlet("/doctor")
public class DoctorServlet extends HttpServlet {
    private static final Set<String> ALLOWED =
            Set.of(Appointment.CONFIRMED, Appointment.COMPLETED, Appointment.CANCELLED);
    private final DoctorDAO doctors = new DoctorDAO();
    private final AppointmentDAO appointments = new AppointmentDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Doctor d = currentDoctor(req);
        if (d == null) { resp.sendRedirect(req.getContextPath() + "/logout"); return; }
        Flash.load(req);
        req.setAttribute("doctor", d);
        req.setAttribute("appointments", appointments.findByDoctor(d.getId()));
        req.getRequestDispatcher("/WEB-INF/views/doctor.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Doctor d = currentDoctor(req);
        if (d == null) { resp.sendRedirect(req.getContextPath() + "/logout"); return; }
        try {
            String status = req.getParameter("status");
            if (status == null || !ALLOWED.contains(status)) throw new IllegalArgumentException("Choose a valid status.");
            int n = appointments.updateStatusForDoctor(Long.parseLong(req.getParameter("id")), d.getId(), status);
            if (n > 0) Flash.set(req, "success", "Appointment marked " + status.toLowerCase() + ".");
            else Flash.set(req, "error", "Appointment not found.");
        } catch (NumberFormatException e) {
            Flash.set(req, "error", "Invalid appointment.");
        } catch (IllegalArgumentException e) {
            Flash.set(req, "error", e.getMessage());
        } catch (RuntimeException e) {
            Flash.set(req, "error", "Update failed: " + e.getMessage());
        }
        resp.sendRedirect(req.getContextPath() + "/doctor");
    }

    private Doctor currentDoctor(HttpServletRequest req) {
        User u = (User) req.getSession().getAttribute("user");
        return doctors.findByUserId(u.getId());
    }
}
