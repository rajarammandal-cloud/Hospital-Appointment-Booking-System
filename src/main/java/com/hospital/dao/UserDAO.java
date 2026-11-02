package com.hospital.dao;

import com.hospital.model.User;
import com.hospital.util.DBUtil;
import com.hospital.util.PasswordUtil;
import java.sql.*;

/** Plain JDBC: accounts and authentication. */
public class UserDAO {

    public void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS users ("
                + "id BIGINT AUTO_INCREMENT PRIMARY KEY, "
                + "username VARCHAR(50) NOT NULL UNIQUE, "
                + "password_hash VARCHAR(200) NOT NULL, "
                + "full_name VARCHAR(100) NOT NULL, "
                + "role VARCHAR(10) NOT NULL)";
        try (Connection c = DBUtil.getConnection(); Statement st = c.createStatement()) {
            st.executeUpdate(sql);
        } catch (SQLException e) { throw wrap(e); }
    }

    public long insert(User u) {
        String sql = "INSERT INTO users (username, password_hash, full_name, role) VALUES (?, ?, ?, ?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPasswordHash());
            ps.setString(3, u.getFullName());
            ps.setString(4, u.getRole());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return rs.getLong(1);
            }
        } catch (SQLException e) { throw wrap(e); }
    }

    public boolean exists(String username) {
        return findByUsername(username) != null;
    }

    public User findByUsername(String username) {
        String sql = "SELECT id, username, password_hash, full_name, role FROM users WHERE username = ?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                User u = new User(rs.getString("username"), rs.getString("password_hash"),
                                  rs.getString("full_name"), rs.getString("role"));
                u.setId(rs.getLong("id"));
                return u;
            }
        } catch (SQLException e) { throw wrap(e); }
    }

    /** Returns the user when the credentials are valid, otherwise null. */
    public User authenticate(String username, String password) {
        User u = findByUsername(username);
        return (u != null && PasswordUtil.verify(password, u.getPasswordHash())) ? u : null;
    }

    public void delete(long id) {
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM users WHERE id = ?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw wrap(e); }
    }

    private static IllegalStateException wrap(SQLException e) {
        return new IllegalStateException("Database error: " + e.getMessage(), e);
    }
}
