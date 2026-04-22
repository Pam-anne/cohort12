<%@ page contentType="text/html; charset=UTF-8" %>
<%
    // Guard: must be logged in
    String username = (String) session.getAttribute("username");
    if (username == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    // Per-session visit counter
    Integer visits = (Integer) session.getAttribute("visits");
    if (visits == null) visits = 0;
    visits++;
    session.setAttribute("visits", visits);
%>
<!DOCTYPE html>
<html>
<head><title>Dashboard</title></head>
<body>
    <h1>Welcome back, <%= username %>!</h1>
    <p>You have visited this dashboard <strong><%= visits %></strong> times in this session.</p>
    <p><a href="form.jsp">Fill Form</a></p>
    <p><a href="logout.jsp">Logout</a></p>
</body>
</html>