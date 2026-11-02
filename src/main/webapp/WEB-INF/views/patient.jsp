<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="My appointments"/>
<%@ include file="header.jspf" %>
<div class="page-head">
  <h1>Hello, <c:out value="${patient.name}"/></h1>
  <p class="muted">Choose a doctor, a day and a time. The doctor confirms your request.</p>
</div>

<div class="split">
  <section class="panel">
    <h2>Book an appointment</h2>
    <form class="stack" method="post" action="${pageContext.request.contextPath}/patient">
      <input type="hidden" name="action" value="book">
      <label class="field">Doctor
        <select name="doctorId" required>
          <option value="" disabled selected>Choose a doctor</option>
          <c:forEach var="d" items="${doctors}">
            <option value="${d.id}"><c:out value="${d.name}"/> - <c:out value="${d.specialization}"/> (&#8377;<fmt:formatNumber value="${d.fee}" maxFractionDigits="2"/>)</option>
          </c:forEach>
        </select>
      </label>
      <label class="field">Date <input type="date" name="date" min="${today}" value="${today}" required></label>
      <fieldset class="slots">
        <legend>Time</legend>
        <div class="chips">
          <c:forEach var="t" items="${slots}" varStatus="st">
            <label class="chip"><input type="radio" name="slot" value="${t}" ${st.first ? 'required' : ''}><span>${t}</span></label>
          </c:forEach>
        </div>
      </fieldset>
      <label class="field">Reason for visit <textarea name="reason" maxlength="255" placeholder="For example: chest pain when climbing stairs"></textarea></label>
      <button class="btn" type="submit">Book appointment</button>
    </form>
  </section>

  <section class="panel">
    <h2>Your appointments</h2>
    <c:choose>
      <c:when test="${empty appointments}">
        <p class="empty">You have no appointments yet. Book your first one using the form.</p>
      </c:when>
      <c:otherwise>
        <div class="table-wrap">
          <table>
            <thead><tr><th>When</th><th>Doctor</th><th>Status</th><th></th></tr></thead>
            <tbody>
            <c:forEach var="a" items="${appointments}">
              <tr>
                <td>${a.appointmentDate}<br><span class="muted small">${a.timeSlot}</span></td>
                <td><c:out value="${a.doctor.name}"/><br><span class="muted small"><c:out value="${a.doctor.specialization}"/></span></td>
                <td><span class="badge s-${a.status}"><c:out value="${a.status}"/></span></td>
                <td>
                  <c:if test="${a.status == 'PENDING' or a.status == 'CONFIRMED'}">
                    <form method="post" action="${pageContext.request.contextPath}/patient"
                          onsubmit="return confirm('Cancel this appointment?');">
                      <input type="hidden" name="action" value="cancel">
                      <input type="hidden" name="id" value="${a.id}">
                      <button class="btn btn-danger btn-sm" type="submit">Cancel</button>
                    </form>
                  </c:if>
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
<%@ include file="footer.jspf" %>
