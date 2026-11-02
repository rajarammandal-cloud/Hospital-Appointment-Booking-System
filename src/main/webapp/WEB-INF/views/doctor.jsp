<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="My schedule"/>
<%@ include file="header.jspf" %>
<div class="page-head">
  <h1><c:out value="${doctor.name}"/></h1>
  <p class="muted"><c:out value="${doctor.specialization}"/> &middot; consultation fee &#8377;<fmt:formatNumber value="${doctor.fee}" maxFractionDigits="2"/></p>
</div>

<section class="panel">
  <h2>Appointments</h2>
  <c:choose>
    <c:when test="${empty appointments}">
      <p class="empty">No patients have booked you yet. New requests will appear here.</p>
    </c:when>
    <c:otherwise>
      <div class="table-wrap">
        <table>
          <thead><tr><th>When</th><th>Patient</th><th>Reason</th><th>Status</th><th>Update</th></tr></thead>
          <tbody>
          <c:forEach var="a" items="${appointments}">
            <tr>
              <td>${a.appointmentDate}<br><span class="muted small">${a.timeSlot}</span></td>
              <td><c:out value="${a.patient.name}"/><br>
                  <span class="muted small"><c:out value="${a.patient.age}"/> yrs, <c:out value="${a.patient.gender}"/>
                  <c:if test="${not empty a.patient.phone}">, <c:out value="${a.patient.phone}"/></c:if></span></td>
              <td><c:out value="${empty a.reason ? '-' : a.reason}"/></td>
              <td><span class="badge s-${a.status}"><c:out value="${a.status}"/></span></td>
              <td>
                <c:if test="${a.status == 'PENDING' or a.status == 'CONFIRMED'}">
                  <form class="actions" method="post" action="${pageContext.request.contextPath}/doctor">
                    <input type="hidden" name="id" value="${a.id}">
                    <c:if test="${a.status == 'PENDING'}">
                      <button class="btn btn-sm" name="status" value="CONFIRMED" type="submit">Confirm</button>
                    </c:if>
                    <c:if test="${a.status == 'CONFIRMED'}">
                      <button class="btn btn-sm" name="status" value="COMPLETED" type="submit">Mark done</button>
                    </c:if>
                    <button class="btn btn-danger btn-sm" name="status" value="CANCELLED" type="submit">Cancel</button>
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
<%@ include file="footer.jspf" %>
