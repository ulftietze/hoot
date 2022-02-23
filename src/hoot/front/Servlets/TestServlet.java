package hoot.front.Servlets;

import hoot.model.entities.User;
import hoot.model.repositories.UserRepository;
import hoot.system.Annotation.AuthenticationRequired;
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
        PrintWriter out = response.getWriter();
        response.setContentType("text/html");

        response.setContentType("text/html");
        out.println("<!doctype html><html>");
        out.println("<head> <meta charset='utf-8'>");
        out.println("<title>JDBC Test</title> </head>");
        out.println("<body>");

        UserRepository ur   = (UserRepository) ObjectManager.get(UserRepository.class);
        User           user = ur.getById(1);

        if (user != null) {
            out.println(
                    "ID: " + user.id + "<br>" +
                    "Username: " + user.username + "<br>" +
                    "ImagePath: " + user.imagePath + "<br>" +
                    "PasswordHash: " + user.passwordHash + "<br>" +
                    "lastLogin: " + user.lastLogin + "<br>" +
                    "created: " + user.created + "<br>"
            );
        } else {
            out.println("DB connection failed or User not found.");
        }

        out.println("</body>");
        out.println("</html>");
    }
}
