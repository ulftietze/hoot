package hoot.front.Servlets;

import hoot.model.entities.Historie;
import hoot.model.entities.Tag;
import hoot.model.entities.User;
import hoot.model.repositories.FollowerRepository;
import hoot.model.repositories.HistorieRepository;
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
        PrintWriter out = response.getWriter();
        response.setContentType("text/html");

        response.setContentType("text/html");
        out.println("<!doctype html><html>");
        out.println("<head> <meta charset='utf-8'>");
        out.println("<title>JDBC Test</title> </head>");
        out.println("<body>");

        /*UserRepository     ur = (UserRepository) ObjectManager.get(UserRepository.class);

        for (int i = 0; i < 4; ++i) {
            try {
                out.println("User " + i + "<br>");

                User user = ur.getById(i);

                out.println(
                        "ID: " + user.id + "<br>" +
                        "Username: " + user.username + "<br>" +
                        "ImagePath: " + user.imagePath + "<br>" +
                        "PasswordHash: " + user.passwordHash + "<br>" +
                        "lastLogin: " + user.lastLogin + "<br>" +
                        "created: " + user.created + "<br>" +
                        "followerCount: " + user.followerCount + "<br>"
                );
                out.println("<br>");
            } catch (EntityNotFoundException e) {
                out.println("DB connection failed or User not found.<br>");
            }
        }*/

        HistorieRepository repository = (HistorieRepository) ObjectManager.create(HistorieRepository.class);

        for (long i = 1; i < 3; ++i) {
            try {
                out.println("Historie: " + i + "<br>\uD83D\uDC08\n<br>");

                Historie historie = repository.getById(i);

                out.println(
                        "ID: " + historie.id + "<br>" +
                        "timestamp: " + historie.timestamp + "<br>" +
                        "currentLoggedIn: " + historie.currentLoggedIn + "<br>" +
                        "postsPerSecond: " + historie.postsPerSecond + "<br>" +
                        "requestsPerSecond: " + historie.requestsPerSecond + "<br>" +
                        "loginsPerSecond: " + historie.loginsPerSecond + "<br>" +
                        "currentlyRegisteredUsers: " + historie.currentlyRegisteredUsers + "<br>"
                );

                out.println("Tags: ");
                if (historie.trendingHashtags != null) {
                    for (Tag tag : historie.trendingHashtags) {
                        out.println(tag.tag + " ");
                    }
                }
                out.println("<br>");
            } catch (EntityNotFoundException e) {
                out.println("DB connection failed or Historie not found.<br>");
            }
        }

        out.println("</body>");
        out.println("</html>");
    }
}
