<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head><title>About</title></head>
<body style="font-family: Arial; margin: 0;">

    <jsp:include page="fragments/header.jsp" />
    <jsp:include page="fragments/navbar.jsp" >
      <jsp:param name="active" value="about" />
    </jsp:include>

    <main style="padding: 30px;">
        <h2>About Us</h2>
        <p>COHORT 12 is a Java training program focused on real-world skills.</p>
        <%-- Pass parameters to the greeting fragment --%>
        <jsp:include page="fragments/greeting.jsp">
            <jsp:param name="user" value="Guest" />
            <jsp:param name="role" value="guest" />
        </jsp:include>
    </main>

    <jsp:include page="fragments/footer.jsp" />

</body>
</html>