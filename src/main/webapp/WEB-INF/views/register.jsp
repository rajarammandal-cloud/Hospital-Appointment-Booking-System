<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Create account"/>
<%@ include file="header.jspf" %>
<section class="auth">
  <div class="panel">
    <h1>Create a patient account</h1>
    <p class="muted">You will use it to book and track appointments.</p>
    <form class="stack" method="post" action="${pageContext.request.contextPath}/register">
      <label class="field">Full name <input name="fullName" maxlength="100" required autofocus></label>
      <div class="row">
        <label class="field">Age <input type="number" name="age" min="0" max="120" required></label>
        <label class="field">Gender
          <select name="gender" required>
            <option value="" disabled selected>Choose</option>
            <option>Female</option><option>Male</option><option>Other</option>
          </select>
        </label>
      </div>
      <label class="field">Phone <input name="phone" maxlength="20" inputmode="tel"></label>
      <label class="field">Username <input name="username" pattern="[A-Za-z0-9_]{3,30}" title="3-30 letters, digits or underscores" autocomplete="username" required></label>
      <label class="field">Password <input type="password" name="password" minlength="6" autocomplete="new-password" required></label>
      <button class="btn" type="submit">Create account</button>
    </form>
    <p class="small muted" style="margin-top:1rem">Already registered? <a href="${pageContext.request.contextPath}/login">Sign in</a></p>
  </div>
</section>
<%@ include file="footer.jspf" %>
