package com.hospital.servlet;

import com.hospital.dao.*;
import com.hospital.model.*;
import com.hospital.util.Flash;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@WebServlet("/patient")
public class PatientServlet extends HttpServlet {
    private final PatientDAO patients = new PatientDAO();
    private final DoctorDAO doctors = new DoctorDAO();
    private final AppointmentDAO appointments = new AppointmentDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Patient p = currentPatient(req);
        if (p == null) { resp.sendRedirect(req.getContextPath() + "/logout"); return; }
        Flash.load(req);
        req.setAttribute("patient", p);
        req.setAttribute("doctors", doctors.findAll());
        req.setAttribute("appointments", appointments.findByPatient(p.getId()));
        req.setAttribute("slots", Appointment.SLOTS);
        req.setAttribute("today", LocalDate.now().toString());
        req.getRequestDispatcher("/WEB-INF/views/patient.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Patient p = currentPatient(req);
        if (p == null) { resp.sendRedirect(req.getContextPath() + "/logout"); return; }
        try {
            if ("book".equals(req.getParameter("action"))) {
                book(req, p);
            } else if ("cancel".equals(req.getParameter("action"))) {
                int n = appointments.cancelForPatient(Long.parseLong(req.getParameter("id")), p.getId());
                if (n > 0) Flash.set(req, "success", "Appointment cancelled.");
                else Flash.set(req, "error", "That appointment can no longer be cancelled.");
            }
        } catch (NumberFormatException | DateTimeParseException e) {
            Flash.set(req, "error", "Some details were missing or invalid. Check the form and try again.");
        } catch (IllegalArgumentException e) {
            Flash.set(req, "error", e.getMessage());
        } catch (RuntimeException e) {
            Flash.set(req, "error", "Action failed: " + e.getMessage());
        }
        resp.sendRedirect(req.getContextPath() + "/patient");
    }

    private void book(HttpServletRequest req, Patient p) {
        long doctorId = Long.parseLong(req.getParameter("doctorId"));
        LocalDate date = LocalDate.parse(req.getParameter("date"));
        String slot = req.getParameter("slot");
        String reason = req.getParameter("reason") == null ? "" : req.getParameter("reason").trim();
        if (reason.length() > 255) reason = reason.substring(0, 255);

        if (date.isBefore(LocalDate.now())) throw new IllegalArgumentException("Pick today or a future date.");
        if (slot == null || !Appointment.SLOTS.contains(slot)) throw new IllegalArgumentException("Pick a time slot.");
        Doctor d = doctors.findById(doctorId);
        if (d == null) throw new IllegalArgumentException("That doctor is no longer available.");
        if (appointments.isSlotTaken(doctorId, date, slot))
            throw new IllegalArgumentException(slot + " on " + date + " is already booked. Choose another slot.");

        appointments.save(new Appointment(p, d, date, slot, reason));
        Flash.set(req, "success", "Booked with " + d.getName() + " on " + date + " at " + slot
                + ". The doctor will confirm it shortly.");
    }

    private Patient currentPatient(HttpServletRequest req) {
        User u = (User) req.getSession().getAttribute("user");
        return patients.findByUserId(u.getId());
    }
}
