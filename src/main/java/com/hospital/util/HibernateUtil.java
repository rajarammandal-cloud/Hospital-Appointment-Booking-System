package com.hospital.util;

import java.util.function.Consumer;
import java.util.function.Function;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

/** Builds the SessionFactory once and offers small transaction helpers. */
public final class HibernateUtil {
    private static SessionFactory factory;

    private HibernateUtil() {}

    public static synchronized SessionFactory get() {
        if (factory == null) {
            factory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
        }
        return factory;
    }

    public static synchronized void shutdown() {
        if (factory != null) { factory.close(); factory = null; }
    }

    /** Read-only work, no transaction needed. */
    public static <T> T read(Function<Session, T> work) {
        try (Session s = get().openSession()) {
            return work.apply(s);
        }
    }

    /** Work inside a transaction, commits on success and rolls back on error. */
    public static <T> T tx(Function<Session, T> work) {
        try (Session s = get().openSession()) {
            Transaction t = s.beginTransaction();
            try {
                T result = work.apply(s);
                t.commit();
                return result;
            } catch (RuntimeException e) {
                if (t.isActive()) t.rollback();
                throw e;
            }
        }
    }

    public static void txRun(Consumer<Session> work) {
        tx(s -> { work.accept(s); return null; });
    }
}
