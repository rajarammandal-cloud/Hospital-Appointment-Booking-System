<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Sign in"/>
<%@ include file="header.jspf" %>
<section class="auth">
  <div class="panel">
    <h1>Sign in</h1>
    <p class="muted">Book a doctor, manage your visits or run the clinic.</p>
    <form class="stack" method="post" action="${pageContext.request.contextPath}/login">
      <label class="field">Username <input name="username" autocomplete="username" required autofocus></label>
      <label class="field">Password <input type="password" name="password" autocomplete="current-password" required></label>
      <button class="btn" type="submit">Sign in</button>
    </form>
    <p class="small muted" style="margin-top:1rem">New patient? <a href="${pageContext.request.contextPath}/register">Create an account</a></p>
    <p class="demo">Demo logins: <code>admin</code> / <code>admin123</code>, <code>drasha</code> / <code>doctor123</code></p>
  </div>
</section>
<%@ include file="footer.jspf" %>
