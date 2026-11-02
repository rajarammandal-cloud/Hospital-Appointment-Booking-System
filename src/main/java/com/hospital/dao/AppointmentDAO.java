package com.hospital.dao;

import com.hospital.model.Appointment;
import com.hospital.util.HibernateUtil;
import java.time.LocalDate;
import java.util.List;

/** Hibernate (HQL with join fetch): appointment persistence and queries. */
public class AppointmentDAO {

    public void save(Appointment a) {
        HibernateUtil.txRun(s -> s.persist(a));
    }

    public boolean isSlotTaken(long doctorId, LocalDate date, String slot) {
        Long n = HibernateUtil.read(s -> s.createQuery(
                "select count(a) from Appointment a where a.doctor.id = :d and a.appointmentDate = :dt "
              + "and a.timeSlot = :t and a.status <> 'CANCELLED'", Long.class)
                .setParameter("d", doctorId).setParameter("dt", date).setParameter("t", slot)
                .getSingleResult());
        return n > 0;
    }

    public List<Appointment> findByPatient(long patientId) {
        return HibernateUtil.read(s -> s.createQuery(
                "select a from Appointment a join fetch a.doctor where a.patient.id = :p "
              + "order by a.appointmentDate desc, a.timeSlot", Appointment.class)
                .setParameter("p", patientId).list());
    }

    public List<Appointment> findByDoctor(long doctorId) {
        return HibernateUtil.read(s -> s.createQuery(
                "select a from Appointment a join fetch a.patient where a.doctor.id = :d "
              + "order by a.appointmentDate, a.timeSlot", Appointment.class)
                .setParameter("d", doctorId).list());
    }

    public List<Appointment> findAll() {
        return HibernateUtil.read(s -> s.createQuery(
                "select a from Appointment a join fetch a.patient join fetch a.doctor "
              + "order by a.appointmentDate desc, a.timeSlot", Appointment.class).list());
    }

    /** Only the doctor who owns the appointment can change its status. */
    public int updateStatusForDoctor(long id, long doctorId, String status) {
        return HibernateUtil.tx(s -> s.createMutationQuery(
                "update Appointment a set a.status = :st where a.id = :id and a.doctor.id = :d")
                .setParameter("st", status).setParameter("id", id).setParameter("d", doctorId)
                .executeUpdate());
    }

    /** A patient can only cancel their own open appointments. */
    public int cancelForPatient(long id, long patientId) {
        return HibernateUtil.tx(s -> s.createMutationQuery(
                "update Appointment a set a.status = 'CANCELLED' where a.id = :id and a.patient.id = :p "
              + "and a.status in ('PENDING', 'CONFIRMED')")
                .setParameter("id", id).setParameter("p", patientId).executeUpdate());
    }
}
