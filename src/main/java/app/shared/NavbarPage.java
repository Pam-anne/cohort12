package app.shared;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/navbar")
public class NavbarPage extends HttpServlet {

    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        PrintWriter writer = resp.getWriter();

        // Navbar CSS
        writer.println("<style>");
        writer.println("body { margin: 0; font-family: Arial, sans-serif; }");
        writer.println(".navbar {");
        writer.println("  display: flex;");
        writer.println("  justify-content: space-between;");
        writer.println("  align-items: center;");
        writer.println("  background-color: #2c3e50;");
        writer.println("  padding: 0 20px;");
        writer.println("  margin-bottom: 20px;");
        writer.println("}");
        writer.println(".logo {");
        writer.println("  color: white;");
        writer.println("  font-size: 20px;");
        writer.println("  font-weight: bold;");
        writer.println("}");
        writer.println(".nav-links { display: flex; gap: 5px; }");
        writer.println(".nav-links a {");
        writer.println("  color: white;");
        writer.println("  text-decoration: none;");
        writer.println("  padding: 15px 12px;");
        writer.println("  font-size: 14px;");
        writer.println("  transition: background 0.3s;");
        writer.println("}");
        writer.println(".nav-links a:hover { background-color: #34495e; }");
        writer.println("</style>");

        // Navbar HTML
        writer.println("<div class='navbar'>");
        writer.println("  <div class='logo'>COHORT 12 PORTAL</div>");
        writer.println("  <div class='nav-links'>");
        writer.println("      <a href='./home'>Home</a>");
        writer.println("      <a href='./aboutus'>About Us</a>");
        writer.println("      <a href='./register_school'>Register School</a>");
        writer.println("      <a href='./school_lists'>View Schools</a>");
        writer.println("      <a href='./register_person'>Register Person</a>");
        writer.println("      <a href='./person_lists'>View Persons</a>");
        writer.println("      <a href='./register_trainer'>Register Trainer</a>");
        writer.println("      <a href='./trainer_lists'>View Trainers</a>");
        writer.println("      <a href='./logout'>Logout</a>");
        writer.println("  </div>");
        writer.println("</div>");
    }
}
