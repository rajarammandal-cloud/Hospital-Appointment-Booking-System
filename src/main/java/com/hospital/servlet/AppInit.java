package com.hospital.servlet;

import com.hospital.dao.DoctorDAO;
import com.hospital.dao.UserDAO;
import com.hospital.model.User;
import com.hospital.service.AccountService;
import com.hospital.util.HibernateUtil;
import com.hospital.util.PasswordUtil;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/** Runs once at startup: builds tables and seeds demo data. */
@WebListener
public class AppInit implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent e) {
        try {
            HibernateUtil.get();                       // Hibernate creates doctors/patients/appointments
            UserDAO users = new UserDAO();
            users.createTable();                       // JDBC creates users
            if (!users.exists("admin")) {
                users.insert(new User("admin", PasswordUtil.hash("admin123"), "Hospital Admin", User.ADMIN));
            }
            if (new DoctorDAO().count() == 0) {
                AccountService a = new AccountService();
                a.registerDoctor("drasha", "doctor123", "Dr. Asha Sharma", "Cardiology", 800);
                a.registerDoctor("drrohan", "doctor123", "Dr. Rohan Mehta", "Dermatology", 500);
                a.registerDoctor("drpriya", "doctor123", "Dr. Priya Singh", "Pediatrics", 600);
            }
        } catch (RuntimeException ex) {
            throw new IllegalStateException("Startup failed. Is MySQL running in XAMPP?", ex);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent e) {
        HibernateUtil.shutdown();
    }
}
