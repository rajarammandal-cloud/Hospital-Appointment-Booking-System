package com.hospital.service;

import com.hospital.dao.DoctorDAO;
import com.hospital.dao.PatientDAO;
import com.hospital.dao.UserDAO;
import com.hospital.model.Doctor;
import com.hospital.model.Patient;
import com.hospital.model.User;
import com.hospital.util.PasswordUtil;

/** Creates a login (JDBC) and its profile (Hibernate) together. */
public class AccountService {
    private final UserDAO users = new UserDAO();
    private final DoctorDAO doctors = new DoctorDAO();
    private final PatientDAO patients = new PatientDAO();

    public void registerPatient(String username, String password, String fullName,
                                int age, String gender, String phone) {
        validateLogin(username, password, fullName);
        if (age < 0 || age > 120) throw new IllegalArgumentException("Enter a valid age.");
        if (!"Male".equals(gender) && !"Female".equals(gender) && !"Other".equals(gender))
            throw new IllegalArgumentException("Choose a gender.");
        long uid = users.insert(new User(username, PasswordUtil.hash(password), fullName, User.PATIENT));
        try {
            patients.save(new Patient(uid, fullName, age, gender, phone));
        } catch (RuntimeException e) {
            users.delete(uid);   // keep both stores consistent
            throw e;
        }
    }

    public void registerDoctor(String username, String password, String fullName,
                               String specialization, double fee) {
        validateLogin(username, password, fullName);
        if (specialization == null || specialization.isBlank())
            throw new IllegalArgumentException("Enter a specialization.");
        if (fee < 0) throw new IllegalArgumentException("Fee cannot be negative.");
        long uid = users.insert(new User(username, PasswordUtil.hash(password), fullName, User.DOCTOR));
        try {
            doctors.save(new Doctor(uid, fullName, specialization.trim(), fee));
        } catch (RuntimeException e) {
            users.delete(uid);
            throw e;
        }
    }

    private void validateLogin(String username, String password, String fullName) {
        if (fullName == null || fullName.isBlank()) throw new IllegalArgumentException("Enter the full name.");
        if (username == null || !username.matches("[A-Za-z0-9_]{3,30}"))
            throw new IllegalArgumentException("Username must be 3-30 letters, digits or underscores.");
        if (password == null || password.length() < 6)
            throw new IllegalArgumentException("Password must be at least 6 characters.");
        if (users.exists(username)) throw new IllegalArgumentException("That username is already taken.");
    }
}
