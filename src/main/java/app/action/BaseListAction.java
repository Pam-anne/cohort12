package app.action;

import app.framework.Cohort12Framework;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;

public abstract class BaseListAction<T> extends BaseAction<T> {

    @Override
    @SuppressWarnings("unchecked")
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // if session exist use it, otherwise create a new one
        HttpSession session = req.getSession();

        PrintWriter writer = resp.getWriter();

        writer.println("<!DOCTYPE html>");
        writer.println("<html>");
        writer.println("<head>");
        writer.println("<title>About Us - Training Academy</title>");
        writer.println("<style>");
        writer.println("body { font-family: Arial; margin: 40px; background-color: #f4f6f8; }");
        writer.println("header { background-color: #2c3e50; color: white; padding: 15px; }");
        writer.println("section { margin-top: 20px; padding: 15px; background: white; border-radius: 5px; }");
        writer.println("a { color: #3498db; text-decoration: none; font-weight: bold; }");
        writer.println("</style>");
        writer.println("</head>");

        writer.println("<body>");

        // Header
        writer.println("<header>");
        writer.println("<h1>About COHORT 12 Training PORTA</h1>");
        writer.println("</header>");

        Cohort12Framework.htmlTable(writer, getType(), returnData(session));

        RequestDispatcher dispatcher = req.getRequestDispatcher("footer");
        dispatcher.include(req, resp);

        writer.println("</body>");
        writer.println("</html>");

    }

}
