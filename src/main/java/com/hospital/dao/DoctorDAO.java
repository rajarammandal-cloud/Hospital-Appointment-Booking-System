package com.hospital.dao;

import com.hospital.model.Doctor;
import com.hospital.util.HibernateUtil;
import java.util.List;

/** Hibernate (HQL): doctor persistence. */
public class DoctorDAO {

    public void save(Doctor d) {
        HibernateUtil.txRun(s -> s.persist(d));
    }

    public List<Doctor> findAll() {
        return HibernateUtil.read(s -> s.createQuery("from Doctor d order by d.name", Doctor.class).list());
    }

    public Doctor findById(long id) {
        return HibernateUtil.read(s -> s.get(Doctor.class, id));
    }

    public Doctor findByUserId(long userId) {
        return HibernateUtil.read(s -> s.createQuery("from Doctor d where d.userId = :u", Doctor.class)
                .setParameter("u", userId).uniqueResult());
    }

    public long count() {
        return HibernateUtil.read(s -> s.createQuery("select count(d) from Doctor d", Long.class).getSingleResult());
    }

    /** Removes the doctor and their appointments in one transaction. */
    public void delete(long id) {
        HibernateUtil.txRun(s -> {
            s.createMutationQuery("delete from Appointment a where a.doctor.id = :id")
                    .setParameter("id", id).executeUpdate();
            s.createMutationQuery("delete from Doctor d where d.id = :id")
                    .setParameter("id", id).executeUpdate();
        });
    }
}
