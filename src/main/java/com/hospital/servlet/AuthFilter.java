package com.hospital.servlet;

import com.hospital.model.User;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;

/** Only a logged-in user with the matching role may open /admin, /doctor or /patient. */
@WebFilter(urlPatterns = {"/admin", "/doctor", "/patient"})
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest rq, ServletResponse rs, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) rq;
        HttpServletResponse resp = (HttpServletResponse) rs;
        HttpSession session = req.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("user");
        String requiredRole = req.getServletPath().substring(1).toUpperCase();

        if (user == null || !requiredRole.equals(user.getRole())) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        // stop the browser showing protected pages from cache after logout
        resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        resp.setHeader("Pragma", "no-cache");
        resp.setDateHeader("Expires", 0);
        chain.doFilter(rq, rs);
    }
}
