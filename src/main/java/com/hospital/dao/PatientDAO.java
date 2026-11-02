package com.hospital.dao;

import com.hospital.model.Patient;
import com.hospital.util.HibernateUtil;

/** Hibernate: patient persistence. */
public class PatientDAO {

    public void save(Patient p) {
        HibernateUtil.txRun(s -> s.persist(p));
    }

    public Patient findByUserId(long userId) {
        return HibernateUtil.read(s -> s.createQuery("from Patient p where p.userId = :u", Patient.class)
                .setParameter("u", userId).uniqueResult());
    }
}
