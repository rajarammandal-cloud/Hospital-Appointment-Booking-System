package com.hospital.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/** Many appointments belong to one patient and one doctor (@ManyToOne). */
@Entity
@Table(name = "appointments")
public class Appointment {
    public static final String PENDING = "PENDING", CONFIRMED = "CONFIRMED",
                               COMPLETED = "COMPLETED", CANCELLED = "CANCELLED";
    public static final List<String> SLOTS =
            List.of("09:00", "10:00", "11:00", "12:00", "14:00", "15:00", "16:00");

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(name = "appointment_date", nullable = false)
    private LocalDate appointmentDate;

    @Column(name = "time_slot", nullable = false, length = 10)
    private String timeSlot;

    @Column(name = "reason", length = 255)
    private String reason;

    @Column(name = "status", nullable = false, length = 15)
    private String status = PENDING;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public Appointment() {}
    public Appointment(Patient patient, Doctor doctor, LocalDate date, String timeSlot, String reason) {
        this.patient = patient; this.doctor = doctor;
        this.appointmentDate = date; this.timeSlot = timeSlot; this.reason = reason;
    }
    public Long getId() { return id; }
    public Patient getPatient() { return patient; }
    public Doctor getDoctor() { return doctor; }
    public LocalDate getAppointmentDate() { return appointmentDate; }
    public String getTimeSlot() { return timeSlot; }
    public String getReason() { return reason; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
