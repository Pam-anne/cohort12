<%@ page contentType="text/html" %>
<%@ page import="java.util.Date" %>
<%@ page import= "java.util.ArrayList" %>

<%-- Declaration is created once,class level --%>
<%! 
 int pageVisits=0;
 ArrayList<String> visitors =new ArrayList<>();
    {
    visitors.add("Pamela");
 visitors.add("Chris");
 visitors.add("Sharon");
    }
%>

<%-- Scriplets runevery request --%>
<%
 pageVisits++;
 Date d=new Date();
 String visitorName=request.getParameter("name");

 if(visitorName==null){
    visitorName="Guest";
 }

%>

<!DOCTYPE html>
<html>
<head>
    <title>Welcome Page</title>

    <style>
        body {
            font-family: Arial, sans-serif;
            background: #f4f7fb;
            margin: 0;
            padding: 0;
        }

        .container {
            max-width: 800px;
            margin: 50px auto;
            background: white;
            padding: 30px;
            border-radius: 12px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.1);
        }

        h1 {
            color: #e3f2fd;
        }

        h2 {
            color: #3498db;
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
            margin: 5px 0;
            padding: 10px;
            border-radius: 6px;
        }

        .highlight {
            font-weight: bold;
            color: #2c3e50;
        }
    </style>
</head>

<body>
    <jsp:include page="fragments/header.jsp"/>

<div class="container">

    <h1 style="color: #2c3e50;">Welcome to COHORT 12, <span class="highlight"><%= visitorName %></span>!</h1>

    <h2>Current time: <%= d %></h2>

    <p>This page has been visited <span class="highlight"><%= pageVisits %></span> times since the server started.</p>

    <p>2 + 2 = <%= 2 + 2 %></p>

    <p>The visitors list size is: <span class="highlight"><%= visitors.size() %></span></p>

    <h3>Visitors List</h3>
    <ul>
        <% for (String v : visitors) { %>
            <li><%= v %></li>
        <% } %>
    </ul>

    <div>
        
        <a href="login.jsp">Login</a>
    </div>
   

</div>
<jsp:include page="fragments/footer.jsp" />
</body>
</html>