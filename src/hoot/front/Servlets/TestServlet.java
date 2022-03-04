package hoot.front.Servlets;

import hoot.model.entities.Historie;
import hoot.model.monitoring.Gnuplotter;
import hoot.system.Annotation.AuthenticationRequired;
import hoot.system.ObjectManager.ObjectManager;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

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

        ArrayList<Historie> historyList = new ArrayList<>();

        int j = 0;
        for (int i = 0; i < 20; ++i) {
            Historie h = (Historie) ObjectManager.create(Historie.class);
            h.timestamp                = LocalDateTime.of(2022, 3, 4, 10, 51, j++);
            h.currentlyRegisteredUsers = ThreadLocalRandom.current().nextInt(1, 100 + 1);
            historyList.add(h);
        }

        Gnuplotter plotter = (Gnuplotter) ObjectManager.create(Gnuplotter.class);

        String url = plotter.createPNGFromHistories(historyList);

        out.println("<img src=\"" + url + "\" alt=\"Graph\"> ");

        out.println("</body>");
        out.println("</html>");
    }
}
