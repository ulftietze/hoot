package hoot.front.Servlets;

import hoot.model.entities.History;
import hoot.model.entities.Tag;
import hoot.model.repositories.HistoryRepository;
import hoot.system.Annotation.AuthenticationRequired;
import hoot.system.Exception.CouldNotSaveException;
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

        HistoryRepository repository = (HistoryRepository) ObjectManager.create(HistoryRepository.class);

        History h = new History();
        h.memoryUsed = 1337;

        try {
            repository.save(h);
        } catch (CouldNotSaveException e) {
            e.printStackTrace();
        }

        out.println(h.id + " " + h.timestamp + "<br><br><br>");

        h.currentLoggedIn = 9999;

        try {
            repository.save(h);
        } catch (CouldNotSaveException e) {
            e.printStackTrace();
        }

        for (long i = 1; i < 10; ++i) {
            try {
                out.println("Historie: " + i);

                History history = repository.getById(i);

                out.println(
                        "ID: " + history.id + "<br>" +
                        "timestamp: " + history.timestamp + "<br>" +
                        "currentLoggedIn: " + history.currentLoggedIn + "<br>" +
                        "requestsPerSecond: " + history.requestsPerSecond + "<br>" +
                        "currentlyRegisteredUsers: " + history.currentlyRegisteredUsers + "<br>"
                );

                out.println("Tags: ");
                if (history.trendingHashtags != null) {
                    for (Tag tag : history.trendingHashtags) {
                        out.println(tag.tag + " ");
                    }
                }
                out.println("<br>");
                out.println("<br>");
            } catch (EntityNotFoundException e) {
                out.println("DB connection failed or Historie not found.<br>");
            }
        }

        out.println("</body>");
        out.println("</html>");
    }
}
