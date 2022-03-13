package hoot.app.Servlets;

import hoot.model.entities.User;
import hoot.model.repositories.UserRepository;
import hoot.system.Annotation.AuthenticationRequired;
import hoot.system.Exception.EntityNotFoundException;
import hoot.system.ObjectManager.ObjectManager;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@AuthenticationRequired(authenticationRequired = false)
@WebServlet("/test")
public class TestServlet extends HttpServlet
{
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        response.setContentType("text/html");

        PrintWriter out = response.getWriter();
        out.println("<!doctype html><html>");
        out.println("<head> <meta charset='utf-8'>");
        out.println("<title>JDBC Test</title> </head>");
        out.println("<body>");

        UserRepository userRepository = (UserRepository) ObjectManager.get(UserRepository.class);

        for (int i = 0; i < 8; ++i) {
            try {
                User user = userRepository.getById(i);
                out.println("User found: ID " + user.id + " name " + user.username + "<br>");
            } catch (EntityNotFoundException e) {
                out.println("User with ID " + i + " was not found.<br>");
            }
        }

        out.println("</body>");
        out.println("</html>");
    }
}
