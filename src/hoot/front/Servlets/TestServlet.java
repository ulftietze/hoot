package hoot.front.Servlets;

import hoot.front.api.dto.user.UserDTO;
import hoot.model.repositories.UserRepository;
import hoot.system.Exception.ConnectionFailedException;
import hoot.system.Exception.EntityNotFoundException;

import javax.naming.NamingException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

@WebServlet("/test")
public class TestServlet extends HttpServlet
{
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        PrintWriter out = response.getWriter();
        response.setContentType("text/html");

        response.setContentType("text/html");
        out.println("<!doctype html><html>");
        out.println("<head> <meta charset='utf-8'>");
        out.println("<title>JDBC Test</title> </head>");
        out.println("<body>");

        UserDTO user = null;

        UserRepository ur = new UserRepository();

        try {
            user = ur.getById(1);
        } catch (EntityNotFoundException | ConnectionFailedException e) {
            e.printStackTrace(out);
        }

        if (user != null) {
            out.println("ID: " + user.id + "<br>" + "UserName: " + user.username + "<br>");
        } else {
            out.println("DB connection failed or User not found.");
        }

        out.println("</body>");
        out.println("</html>");
    }
}
