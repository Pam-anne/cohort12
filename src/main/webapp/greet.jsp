<%@ page contentType="text/html" %>
<%@ page import="java.util.*" %>

<%
    // Using request — single value
    String name = request.getParameter("name");
    String age = request.getParameter("age");
    String language=request.getParameter("language");

    // Using request — multiple values (checkboxes)
    String[] hobbies = request.getParameterValues("hobby");

        if (name==null || name.isEmpty()){
            response.sendRedirect("form.jsp");
            return;
        }
    // Using request — header info
    String browser = request.getHeader("User-Agent");

    // Setting an attribute (server-side data)
    request.setAttribute("serverTime", new Date());
%>

<!DOCTYPE html>
<html>
<head>
    <title>Greeting</title>

    <style>
        body {
            font-family: Arial, sans-serif;
            background: #f4f7fb;
            margin: 0;
            padding: 0;
        }

        .container {
            max-width: 800px;
            margin: 40px auto;
            background: white;
            padding: 25px;
            border-radius: 12px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
        }

        h1 {
            color: #2c3e50;
        }

        h3 {
            color: #3498db;
            margin-top: 20px;
        }

        p {
            font-size: 16px;
            color: #444;
        }

        ul {
            list-style: none;
            padding: 0;
        }

        li {
            background: #e3f2fd;
            margin: 6px 0;
            padding: 10px;
            border-radius: 6px;
        }

        .card {
            background: #f9fafc;
            padding: 15px;
            border-radius: 8px;
            margin-top: 10px;
        }

        .highlight {
            font-weight: bold;
            color: #2c3e50;
        }

        code {
            background: #eee;
            padding: 2px 5px;
            border-radius: 4px;
        }
    </style>
</head>

<body>

<div class="container">

    <h1>Hello, <span class="highlight">${param.name}</span>!</h1>

    <p>You are <span class="highlight"><%= age %></span> years old.</p>

    <h3>Your hobbies:</h3>
    <ul>
        <%
            if (hobbies != null) {
                for (String h : hobbies) {
        %>
                    <li><%= h %></li>
        <%
                }
            } else {
        %>
            <li>None selected</li>
        <%
            }
        %>
    </ul>

    <h3>Your favourite Language:</h3>
    <p class="card"><%= language %></p>

    <h3>Using <code>out</code> directly:</h3>
    <p class="card">
        <% out.print("This was written using out.print()"); %>
    </p>

    <h3>Request details:</h3>
    <p class="card">Your browser: <%= browser %></p>
    <p class="card">Server time: <%= request.getAttribute("serverTime") %></p>

    <h3>All parameters:</h3>
    <ul>
        <%
            Map<String, String[]> params = request.getParameterMap();
            for (Map.Entry<String, String[]> entry : params.entrySet()) {
        %>
                <li><%= entry.getKey() %> = <%= Arrays.toString(entry.getValue()) %></li>
        <%
            }
        %>
    </ul>

</div>

</body>
</html>