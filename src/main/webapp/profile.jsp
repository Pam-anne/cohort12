<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="app.model.UserJSP, java.util.Arrays" %>


<%
    UserJSP user = new UserJSP("Pamela", 20, "admin", 
                        Arrays.asList("Coding", "Music", "Hiking"));
    request.setAttribute("user", user);
%>
<jsp:useBean id="today" class="java.util.Date" />


<!DOCTYPE html>
<html>
<head>
    <title>Profile</title>
    <style>
        body { font-family: Arial; padding: 30px; background: #f4f7fb; }
        .card { background: white; padding: 20px; border-radius: 8px; max-width: 500px; }
        .badge { background: #3498db; color: white; padding: 3px 8px; border-radius: 4px; }
        .badge.admin { background: #e74c3c; }
        .first { font-weight: bold; }
        .last { font-style: italic; }
    </style>
</head>
<body>
    <div class="card">
        <h1>${user.name}'s Profile</h1>

        <%-- Use c:set to create a derived variable --%>
        <c:set var="canVote" value="${user.age >= 18}" />
        <c:set var="ageCategory">
            <c:choose>
                <c:when test="${user.age < 13}">Child</c:when>
                <c:when test="${user.age < 18}">Teen</c:when>
                <c:when test="${user.age < 65}">Adult</c:when>
                <c:otherwise>Senior</c:otherwise>
            </c:choose>
        </c:set>

        <p>Age: ${user.age}</p>
        <p>Category: ${ageCategory}</p>

        <%-- c:if for a single-branch conditional --%>
        <p>Status: 
            <span class="badge <c:if test='${user.role == "admin"}'>admin</c:if>">
                ${user.role}
            </span>
        </p>

        <%-- c:choose replaces multi-branch logic --%>
        <p>Permission level: 
            <c:choose>
                <c:when test="${user.role == 'admin'}">Full access</c:when>
                <c:when test="${user.role == 'editor'}">Can edit content</c:when>
                <c:when test="${user.role == 'viewer'}">Read-only</c:when>
                <c:otherwise>No role assigned</c:otherwise>
            </c:choose>
        </p>

        <h3>Hobbies :</h3>

        <%-- c:if to handle the empty case --%>
        <c:if test="${empty user.hobbies}">
            <p><em>No hobbies listed.</em></p>
        </c:if>

        <%-- c:forEach replaces the hardcoded indexes --%>
        <c:if test="${not empty user.hobbies}">
            <ul>
                <c:forEach var="hobby" items="${user.hobbies}" varStatus="status">
                    <li class="${status.first ? 'first' : ''} ${status.last ? 'last' : ''}">
                        ${status.count}. ${hobby}
                        <c:if test="${status.first}"> (first!)</c:if>
                        <c:if test="${status.last}"> (last!)</c:if>
                    </li>
                </c:forEach>
            </ul>
        </c:if>

        <h3>Conditional messages</h3>

        <%-- Compare: ternary EL vs c:choose --%>
        <p>Name status (EL ternary): 
           ${empty user.name ? 'No name set' : 'Name is set'}</p>
        <p>Name: 
        <c:choose>
            <c:when test="${empty user.name}">Set Your Name</c:when>
            <c:otherwise>Your name is set as ${user.name}</c:otherwise>

        </c:choose>
        </p>
        <p>Name status (c:choose):
           <c:choose>
               <c:when test="${empty user.name}">No name set</c:when>
               <c:otherwise>Name is set</c:otherwise>
           </c:choose>
        </p>

        <p>Double age: ${user.age * 2}</p>
        <p>Can vote? ${canVote}</p>
        <c:if test="${canVote}">
            <p> You're eligible to vote.</p>
        </c:if>
        <c:if test="${not canVote}">
            <p>You're not yet eligible to vote.</p>
        </c:if>

        <p>Date:
            <fmt:formatDate value="${today}" pattern="dd-MM-yyyy"/>

        </p>

    </div>
</body>
</html>