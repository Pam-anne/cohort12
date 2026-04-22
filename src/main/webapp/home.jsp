<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page isELIgnored="false" %>
<!DOCTYPE html>
<html>
<head>
    <title>Home</title>
    <style>
        body { font-family: Arial; margin: 0; }
        main { padding: 30px; }
        .el-demo { background: #f4f7fb; padding: 15px; border-radius: 8px; 
                   margin: 15px 0; border-left: 4px solid #3498db; }
        .el-demo h3 { margin-top: 0; color: #2c3e50; }
        table { border-collapse: collapse; width: 100%; max-width: 600px; }
        th, td { padding: 8px 12px; border: 1px solid #ddd; text-align: left; }
        th { background: #3498db; color: white; }
        code { background: #ecf0f1; padding: 2px 6px; border-radius: 3px; 
               font-family: monospace; }
    </style>
</head>
<body>

    <jsp:include page="fragments/header.jsp" />
    <jsp:include page="fragments/navbar.jsp">
        <jsp:param name="active" value="home" />
    </jsp:include>

    <main>
        <h2>Welcome Home</h2>

        <jsp:include page="fragments/greeting.jsp">
            <jsp:param name="user" value="Pamela" />
            <jsp:param name="role" value="admin" />
        </jsp:include>

        <jsp:useBean id="userjsp" class="app.model.UserJSP" />
        <jsp:setProperty name="userjsp" property="name" value="Pamela Abaki" />
        <jsp:setProperty name="userjsp" property="age" value="60" />
        <jsp:setProperty name="userjsp" property="bonus" value="10" />

        <p>Name: <strong>${userjsp.name}</strong></p> 
         <p>Age: <strong>${userjsp.age}</strong></p> 
           <p>Bonus: <strong> <jsp:getProperty name="userjsp" property="bonus"/> </strong></p>

        <%-- ============= ARITHMETIC OPERATORS ============= --%>
        <div class="el-demo">
            <h3>Arithmetic operators</h3>
            <table>
                <tr><th>Operator</th><th>Result</th></tr>
                <tr><td>+</td> <td>${20 + 10}</td></tr>
                <tr><td>-</td>  <td>${20 - 10}</td></tr>
                <tr><td>*</td>  <td>${20 * 10}</td></tr>
                <tr><td>/</td>  <td>${20 / 10}</td></tr>
                <tr><td>%</td>  <td>${20 % 3}</td></tr>
                <tr><td>mod</td> <td>${20 mod 3}</td></tr>
            </table>

            <h4>Using bean properties</h4>
            <table>
                <tr><th>Operator</th><th>Result</th></tr>
                <tr><td>+</td>  <td>${userjsp.age + userjsp.bonus}</td></tr>
                <tr><td>-</td>  <td>${userjsp.age - userjsp.bonus}</td></tr>
                <tr><td>*</td>  <td>${userjsp.age * userjsp.bonus}</td></tr>
                <tr><td>/</td>  <td>${userjsp.age / userjsp.bonus}</td></tr>
                <tr><td>mod</td><td>${userjsp.age mod userjsp.bonus}</td></tr>
            </table>
        </div>

        <%-- ============= RELATIONAL OPERATORS ============= --%>
        <div class="el-demo">
            <h3>Relational operators (symbol and word versions do the same thing)</h3>
            <table>
                <tr><th>Symbol</th><th>Result</th></tr>
                <tr><td>==</td><td>${userjsp.age == 60}</td></tr>
                <tr><td>eq</td><td>${userjsp.age eq 60}</td></tr>
                <tr><td>!=</td><td>${userjsp.age != 18}</td></tr>
                <tr><td>ne</td>><td>${userjsp.age ne 18}</td></tr>
                <tr><td>&lt;</td><td>${userjsp.age < 100}</td></tr>
                <tr><td>lt</td><td>${100 lt userjsp.age }</td></tr>
                <tr><td>&gt;</td><td>${18>userjsp.age }</td></tr>
                <tr><td>gt</td><td>${userjsp.age gt 18}</td></tr>
                <tr><td>&lt;=</td><td>${userjsp.age <= 60}</td></tr>
                <tr><td>le</td><td>${userjsp.age le 60}</td></tr>
                <tr><td>&gt;=</td><td>${2>=userjsp.age}</td></tr>
                <tr><td>ge</td><td>${userjsp.age ge 18}</td></tr>
            </table>

            <h4>String comparison</h4>
            <p>Is("userjsp.name == Pamela Abaki"): ${userjsp.name == 'Pamela Abaki'}</p>
            <p>Is("userjsp.name eq Pamela Abaki"): ${userjsp.name eq 'Pamela Abaki'}</p>
            <p>Is("userjsp.name not Equal Chris"): ${userjsp.name ne 'Chris'}</p>
        </div>

        <%-- ============= LOGICAL OPERATORS (BONUS) ============= --%>
        <div class="el-demo">
            <h3>Logical operators</h3>
            <p>("userjsp.age > 18 and userjsp.age < 100"):
               ${userjsp.age > 18 and userjsp.age < 100}</p>
            <p>("userjsp.age > 100 or userjsp.bonus > 5"): 
               ${userjsp.age > 100 or userjsp.bonus > 5}</p>
            <p>("not (userjsp.age == 60)"):
               ${not (userjsp.age == 60)}</p>
            </div>

    </main>

    <jsp:include page="fragments/footer.jsp" />

</body>
</html>