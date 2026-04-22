package app.action;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/home")
public class HomePage extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<title>Home - Training Academy</title>");
        out.println("<style>");
        out.println("body { font-family: Arial; margin: 0; background-color: #f4f6f8; }");
        out.println("section { margin: 20px 40px; padding: 20px; background: white; border-radius: 5px; }");
        out.println("h1 { color: #2c3e50; }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");

        // Include navbar
        request.getRequestDispatcher("navbar").include(request, response);

        // Dashboard content
        out.println("<section>");
        out.println("<h1>Welcome to COHORT 12 Training Portal</h1>");
        out.println(
                "<p>You are logged in and can manage schools, persons, and trainers </p>");
        out.println("</section>");

        out.println("</body>");
        out.println("</html>");
    }
}
