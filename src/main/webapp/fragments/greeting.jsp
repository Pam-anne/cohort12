<%@ page contentType="text/html; charset=UTF-8" %>
<div style="background: #ecf0f1; padding: 15px; border-radius: 5px; margin: 10px 0;">
    <p>Hello, <strong><%= request.getParameter("user") %></strong>! 
       You're logged in as <em><%= request.getParameter("role") %></em>.</p>
</div>