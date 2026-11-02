package com.hospital.dao;

import com.hospital.model.Appointment;
import com.hospital.util.DBUtil;
import java.sql.*;
import java.util.*;

/** Plain JDBC: aggregate SQL for the admin dashboard. */
public class ReportDAO {

    public Map<String, Integer> totals() {
        String sql = "SELECT (SELECT COUNT(*) FROM doctors), (SELECT COUNT(*) FROM patients), "
                   + "(SELECT COUNT(*) FROM appointments)";
        Map<String, Integer> m = new LinkedHashMap<>();
        try (Connection c = DBUtil.getConnection(); Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            rs.next();
            m.put("doctors", rs.getInt(1));
            m.put("patients", rs.getInt(2));
            m.put("appointments", rs.getInt(3));
        } catch (SQLException e) { throw wrap(e); }
        return m;
    }

    public Map<String, Integer> statusCounts() {
        Map<String, Integer> m = new LinkedHashMap<>();
        for (String s : List.of(Appointment.PENDING, Appointment.CONFIRMED,
                                Appointment.COMPLETED, Appointment.CANCELLED)) m.put(s, 0);
        try (Connection c = DBUtil.getConnection(); Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT status, COUNT(*) FROM appointments GROUP BY status")) {
            while (rs.next()) m.put(rs.getString(1), rs.getInt(2));
        } catch (SQLException e) { throw wrap(e); }
        return m;
    }

    /** Each row: {doctor name, specialization, active appointment count}. */
    public List<String[]> appointmentsPerDoctor() {
        String sql = "SELECT d.name, d.specialization, COUNT(a.id) AS total "
                   + "FROM doctors d LEFT JOIN appointments a "
                   + "ON a.doctor_id = d.id AND a.status <> 'CANCELLED' "
                   + "GROUP BY d.id, d.name, d.specialization ORDER BY total DESC, d.name";
        List<String[]> rows = new ArrayList<>();
        try (Connection c = DBUtil.getConnection(); Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) rows.add(new String[]{rs.getString(1), rs.getString(2), String.valueOf(rs.getInt(3))});
        } catch (SQLException e) { throw wrap(e); }
        return rows;
    }

    private static IllegalStateException wrap(SQLException e) {
        return new IllegalStateException("Database error: " + e.getMessage(), e);
    }
}
