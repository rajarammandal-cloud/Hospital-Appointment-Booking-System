package com.hospital.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/** One-time messages that survive a redirect (post/redirect/get). */
public final class Flash {
    private Flash() {}

    public static void set(HttpServletRequest req, String type, String message) {
        HttpSession s = req.getSession();
        s.setAttribute("flashType", type);
        s.setAttribute("flashMsg", message);
    }

    public static void load(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        if (s == null || s.getAttribute("flashMsg") == null) return;
        req.setAttribute("flashMsg", s.getAttribute("flashMsg"));
        req.setAttribute("flashType", s.getAttribute("flashType"));
        s.removeAttribute("flashMsg");
        s.removeAttribute("flashType");
    }
}
