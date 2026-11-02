<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Admin"/>
<%@ include file="header.jspf" %>
<div class="page-head">
  <h1>Hospital overview</h1>
  <p class="muted">Manage doctors and watch how appointments are moving.</p>
</div>

<div class="summary">
  <div><strong>${totals.doctors}</strong><span>Doctors</span></div>
  <div><strong>${totals.patients}</strong><span>Patients</span></div>
  <div><strong>${totals.appointments}</strong><span>Appointments</span></div>
  <c:forEach var="e" items="${statusCounts}">
    <div><strong>${e.value}</strong><span><span class="badge s-${e.key}"><c:out value="${e.key}"/></span></span></div>
  </c:forEach>
</div>

<div class="split">
  <section class="panel">
    <h2>Add a doctor</h2>
    <form class="stack" method="post" action="${pageContext.request.contextPath}/admin">
      <input type="hidden" name="action" value="addDoctor">
      <label class="field">Full name <input name="fullName" maxlength="100" placeholder="Dr. Meera Kapoor" required></label>
      <div class="row">
        <label class="field">Specialization <input name="specialization" maxlength="100" required></label>
        <label class="field">Fee (&#8377;) <input type="number" name="fee" min="0" step="50" value="500" required></label>
      </div>
      <div class="row">
        <label class="field">Login username <input name="username" pattern="[A-Za-z0-9_]{3,30}" required></label>
        <label class="field">Password <input type="password" name="password" minlength="6" required></label>
      </div>
      <button class="btn" type="submit">Add doctor</button>
    </form>
  </section>

  <section class="panel">
    <h2>Doctors</h2>
    <c:choose>
      <c:when test="${empty doctors}"><p class="empty">No doctors yet. Add one using the form.</p></c:when>
      <c:otherwise>
        <div class="table-wrap">
          <table>
            <thead><tr><th>Name</th><th>Specialization</th><th class="num">Fee</th><th></th></tr></thead>
            <tbody>
            <c:forEach var="d" items="${doctors}">
              <tr>
                <td><c:out value="${d.name}"/></td>
                <td><c:out value="${d.specialization}"/></td>
                <td class="num">&#8377;<fmt:formatNumber value="${d.fee}" maxFractionDigits="2"/></td>
                <td>
                  <form method="post" action="${pageContext.request.contextPath}/admin"
                        onsubmit="return confirm('Remove this doctor and all their appointments?');">
                    <input type="hidden" name="action" value="deleteDoctor">
                    <input type="hidden" name="id" value="${d.id}">
                    <button class="btn btn-danger btn-sm" type="submit">Remove</button>
                  </form>
                </td>
              </tr>
            </c:forEach>
            </tbody>
          </table>
        </div>
      </c:otherwise>
    </c:choose>
  </section>
</div>

<section class="panel">
  <h2>Active appointments per doctor</h2>
  <div class="table-wrap">
    <table>
      <thead><tr><th>Doctor</th><th>Specialization</th><th class="num">Appointments</th></tr></thead>
      <tbody>
      <c:forEach var="row" items="${perDoctor}">
        <tr><td><c:out value="${row[0]}"/></td><td><c:out value="${row[1]}"/></td><td class="num"><c:out value="${row[2]}"/></td></tr>
      </c:forEach>
      </tbody>
    </table>
  </div>
</section>

<section class="panel">
  <h2>All appointments</h2>
  <c:choose>
    <c:when test="${empty appointments}"><p class="empty">No appointments have been booked yet.</p></c:when>
    <c:otherwise>
      <div class="table-wrap">
        <table>
          <thead><tr><th>When</th><th>Patient</th><th>Doctor</th><th>Reason</th><th>Status</th></tr></thead>
          <tbody>
          <c:forEach var="a" items="${appointments}">
            <tr>
              <td>${a.appointmentDate} <span class="muted small">${a.timeSlot}</span></td>
              <td><c:out value="${a.patient.name}"/></td>
              <td><c:out value="${a.doctor.name}"/></td>
              <td><c:out value="${empty a.reason ? '-' : a.reason}"/></td>
              <td><span class="badge s-${a.status}"><c:out value="${a.status}"/></span></td>
            </tr>
          </c:forEach>
          </tbody>
        </table>
      </div>
    </c:otherwise>
  </c:choose>
</section>
<%@ include file="footer.jspf" %>
