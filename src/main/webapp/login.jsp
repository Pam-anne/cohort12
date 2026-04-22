<%@ page contentType="text/html; charset=UTF-8" %>
<%
    // GUARD & LOGIC FIRST — before any HTML
    String errorMsg = null;

    if ("POST".equalsIgnoreCase(request.getMethod())) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if ("admin".equals(username) && "1234".equals(password)) {
            session.setAttribute("username", username);
            response.sendRedirect("dashboard.jsp");
            return;  // STOP executing this page
        } else {
            errorMsg = "Invalid username or password";
        }
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>Login</title>
    <style>
        body { font-family: Arial; background: #f4f7fb; 
               display: flex; justify-content: center; align-items: center; height: 100vh; }
        .box { background: white; padding: 30px; border-radius: 10px; 
               box-shadow: 0 4px 10px rgba(0,0,0,0.1); width: 300px; }
        input { width: 100%; padding: 10px; margin: 8px 0; box-sizing: border-box; }
        button { width: 100%; padding: 10px; background: #3498db; color: white; 
                 border: none; border-radius: 5px; cursor: pointer; }
        .error { color: red; margin-top: 10px; }
    </style>
</head>
<body>
    <div class="box">
        <h2>Login</h2>
        <form action="login.jsp" method="POST">
            <input type="text" name="username" placeholder="Username" required>
            <input type="password" name="password" placeholder="Password" required>
            <button type="submit">Login</button>
        </form>

        <% if (errorMsg != null) { %>
            <p class="error"><%= errorMsg %></p>
        <% } %>
    </div>
</body>
</html>