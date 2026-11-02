package com.hospital.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/** Plain JDBC connection factory (reads db.properties). */
public final class DBUtil {
    private static final Properties P = new Properties();

    static {
        try (InputStream in = DBUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
            P.load(in);
            Class.forName(P.getProperty("db.driver"));
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private DBUtil() {}

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                P.getProperty("db.url"), P.getProperty("db.user"), P.getProperty("db.password"));
    }
}
