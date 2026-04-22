<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page isELIgnored ="false" %>
<%@ page import="app.utility.Courses" %>
<%@ page import="java.time.LocalTime" %>


<%! Courses courses = new Courses(); %>
<!DOCTYPE html>
<html>
<head>
    <title>Training Portal</title>
    <style>
        body {
            font-family: Arial;
            margin: 40px;
            background-color: #f4f6f8;
        }
        header {
            background-color: #2c3e50;
            color: white;
            padding: 15px;
        }
        section {
            margin-top: 20px;
            padding: 15px;
            background: white;
            border-radius: 5px;
        }
        a {
            color: #3498db;
            text-decoration: none;
            font-weight: bold;
        }
    </style>
</head>

<body>

    <%-- Header --%>
    <header>
        <h1>
        <%
            LocalTime now = LocalTime.now();
            int hour = now.getHour();

            if (hour < 12){
        %>
        Good Morning<br/>
        <%
            } else {
        %>
        Good Afternoon<br/>
        <%
            }
        %>
        Welcome to <%= application.getInitParameter("applicationName") %> Training PORTAL</h1>
        <p>Empowering Developers with Real-World Skills</p>
        <%!

            public int addTotalSum(){
                int totalSum = 0;
                for (int x = 0; x< 20; x++)
                    totalSum += x;

                return totalSum;
            }
        %>
        <p>
        <%= addTotalSum() %>
        </p>
    </header>

    <%-- Courses section --%>
    <section>
        <h2>Available Courses</h2>
        <ul>
            <%= courses.list() %>
        </ul>
    </section>

    <!-- About training -->
    <section>
        <h2>About Our Training</h2>
        <%
            String [] aboutInfos = {
                "<p>Our training programs focus on hands-on experience and real-world projects.</p>",
                "<p>We help developers build strong backend systems using modern technologies.</p>",
                "<p>Mentorship and practical coding sessions are at the core of our learning approach.</p>"};

            for (String aboutInfo: aboutInfos)
                out.print(aboutInfo);

        %>

    </section>

    <!-- Schedule -->
    <section>
        <h2>Upcoming Schedule</h2>
        <jsp:useBean id="weekday" class="app.model.Schedule" />
        <jsp:setProperty name="weekday" property="scheduleType" value="Weekday Classes:" />
        <jsp:setProperty name="weekday" property="scheduleTime" value="6:00 PM - 8:00 PM" />
        <p><jsp:getProperty name="weekday" property="scheduleType" />
            ${weekday.scheduleTime}
        <!-- <jsp:getProperty name="weekday" property="scheduleTime"  /> -->
      </p>
        
        <p>Weekend Bootcamps: 9:00 AM - 1:00 PM</p>
    </section>

    <jsp:include page="footer.jsp" />

</body>
</html>