package app.action;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebInitParam;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "Login",
    urlPatterns = { "/login" },
    initParams = {
            @WebInitParam(name = "username", value = "admin"),
            @WebInitParam(name = "password", value = "12345")
    })
public class LoginPage extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html lang='en'>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<title>Login</title>");

        out.println("<style>");
        out.println("body { font-family: Arial; background:#f4f6f9; display:flex; justify-content:center; align-items:center; height:100vh; }");
        out.println(".login-container { background:#fff; padding:30px; border-radius:8px; box-shadow:0 4px 12px rgba(0,0,0,0.1); width:300px; }");
        out.println(".login-container h2 { text-align:center; margin-bottom:20px; }");
        out.println(".form-group { margin-bottom:15px; }");
        out.println("label { display:block; margin-bottom:5px; font-size:14px; }");
        out.println("input { width:100%; padding:10px; border:1px solid #ccc; border-radius:4px; }");
        out.println(".login-btn { width:100%; padding:10px; background:#007bff; border:none; color:#fff; font-size:16px; border-radius:4px; cursor:pointer; }");
        out.println(".login-btn:hover { background:#0056b3; }");
        out.println(".footer { text-align:center; margin-top:10px; font-size:13px; }");
        out.println(".footer a { color:#007bff; text-decoration:none; }");
        out.println("</style>");

        out.println("</head>");
        out.println("<body>");

        out.println("<div class='login-container'>");
        out.println("<h2>Login</h2>");

        out.println("<form action='login' method='POST'>");

        out.println("<div class='form-group'>");
        out.println("<label for='username'>Username</label>");
        out.println("<input type='text' id='username' name='username' required>");
        out.println("</div>");

        out.println("<div class='form-group'>");
        out.println("<label for='password'>Password</label>");
        out.println("<input type='password' id='password' name='password' required>");
        out.println("</div>");

        out.println("<button type='submit' class='login-btn'>Sign In</button>");

        out.println("<div class='footer'>");
        out.println("<p><a href='#'>Forgot password?</a></p>");
        out.println("</div>");

        out.println("</form>");
        out.println("</div>");

        out.println("</body>");
        out.println("</html>");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        ServletConfig config = getServletConfig();
        ServletContext context = getServletContext();

        String username = request.getParameter("username");
        if (username == null)
            username = "";
        String password = request.getParameter("password");
        if  (password == null)
            password = "";

        System.out.println("submitted: " + username + " && " + password);

        String usernameConfig = config.getInitParameter("username");
        String passwordConfig = config.getInitParameter("password");
        System.out.println("configs: username - " + usernameConfig);
        System.out.println("configs: password - " + passwordConfig);

        if(username.equalsIgnoreCase(usernameConfig)
                && password.equalsIgnoreCase(passwordConfig)){
            //you must create new session
            HttpSession session = request.getSession(true);
            session.setAttribute("UserActualName", "Mike Bavon");
            System.out.println("session.getId(): " + session.getId());
            session.setAttribute("SESSION_ID", session.getId());

            response.sendRedirect("./home");

        } else {
            request.getSession().invalidate();
            response.sendRedirect("./login");
        }
    }
}