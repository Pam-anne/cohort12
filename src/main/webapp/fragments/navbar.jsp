<%@ page contentType="text/html; charset=UTF-8" %>
<%!
    // Declaration: a reusable helper method (runs once at class load)
    private String styleFor(String link, String active) {
        String base = "color: white; margin-right: 15px; text-decoration: none;";
        if (link.equals(active)) {
            return base + " color: #f1c40f; font-weight: bold; border-bottom: 2px solid #f1c40f;";
        }
        return base;
    }
%>
<%
    String active = request.getParameter("active");
    if (active == null) active = "";
%>
<nav style="background: #34495e; padding: 10px;">
    <a href="home.jsp"    style="<%= styleFor("home", active) %>">Home</a>
    <a href="about.jsp"   style="<%= styleFor("about", active) %>">About</a>
    <a href="profile.jsp" style="<%= styleFor("profile", active) %>">Profile</a>
</nav>