<%
// Guard: must be logged in
    String username = (String) session.getAttribute("username");
    if (username == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    %>
<!DOCTYPE html>
<html>

<head>
    <title>Register</title>

    <style>
        body {
            font-family: Arial, sans-serif;
            background: linear-gradient(to right, #74ebd5, #9face6);
            margin: 0;
            padding: 0;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
        }

        .container {
            background: white;
            padding: 30px;
            border-radius: 12px;
            box-shadow: 0 5px 20px rgba(0,0,0,0.2);
            width: 400px;
        }

        h1 {
            text-align: center;
            margin-bottom: 20px;
            color: #333;
        }

        label {
            font-weight: bold;
        }

        input[type="text"],
        input[type="number"],
        select {
            width: 100%;
            padding: 10px;
            margin: 8px 0 15px 0;
            border: 1px solid #ccc;
            border-radius: 6px;
        }

        section {
            margin-bottom: 15px;
        }

        input[type="checkbox"] {
            margin-right: 6px;
        }

        button {
            width: 100%;
            padding: 10px;
            background: #4facfe;
            color: white;
            border: none;
            border-radius: 6px;
            font-size: 16px;
            cursor: pointer;
        }

        button:hover {
            background: #00c6ff;
        }

        .hobbies {
            margin-top: 8px;
        }
    </style>
</head>

<body>

<div class="container">

    <h1>Tell us about yourself</h1>

    <form action="greet.jsp" method="GET">

        <label>Name:</label>
        <input type="text" name="name">

        <label>Age:</label>
        <input type="number" name="age">

        <section>
            <label>Hobbies:</label><br>
            <div class="hobbies">
                <input type="checkbox" name="hobby" value="Coding"> Coding<br>
                <input type="checkbox" name="hobby" value="Music"> Music<br>
                <input type="checkbox" name="hobby" value="Sports"> Sports<br>
            </div>
        </section>

        <label for="language">Favourite Language:</label>
        <select id="language" name="language">
            <option value="luo">Luo</option>
            <option value="english">English</option>
            <option value="kiswahili">Kiswahili</option>
        </select>

        <button type="submit">Submit</button>

    </form>

</div>
 
</body>

</html>