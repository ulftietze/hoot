package hoot.front.Servlets;

import hoot.model.entities.History;
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
        response.setContentType("text/html");

        PrintWriter out = response.getWriter();
        out.println("<!doctype html><html>");
        out.println("<head> <meta charset='utf-8'>");
        out.println("<title>JDBC Test</title> </head>");
        out.println("<body>");

        ArrayList<History> historyList = new ArrayList<>();

        for (int i = 0; i < 59; ++i) {
            History h = (History) ObjectManager.create(History.class);

            h.id = (long) i;

            h.timestamp = LocalDateTime.of(2022, 3, 4, 10, 51, i);

            h.currentLoggedIn          = ThreadLocalRandom.current().nextInt(15, 30 + 1);
            h.currentlyRegisteredUsers = ThreadLocalRandom.current().nextInt(80, 100 + 1);
            h.memoryMax                = 1024;
            h.memoryTotal              = 1000;
            h.memoryFree               = ThreadLocalRandom.current().nextInt(100, 900 + 1);
            h.memoryUsed               = h.memoryTotal - h.memoryFree;
            h.threadCount              = ThreadLocalRandom.current().nextInt(1, 20 + 1);
            h.threadCountTotal         = 5 * i;

            h.loginsPerSixHours         = 50 + ThreadLocalRandom.current().nextFloat() * 50;
            h.registrationsPerSixHours  = 10 + ThreadLocalRandom.current().nextFloat() * 10;
            h.postsPerMinute            = 20 + ThreadLocalRandom.current().nextFloat() * 15;
            h.requestsPerSecond         = 75 + ThreadLocalRandom.current().nextFloat() * 50;
            h.requestsLoggedInPerSecond = 5 + ThreadLocalRandom.current().nextFloat() * 10;

            h.systemLoadAverage = ThreadLocalRandom.current().nextDouble() * 4;
            h.systemCPULoad     = ThreadLocalRandom.current().nextDouble() * 1;
            h.processCPULoad    = ThreadLocalRandom.current().nextDouble() * 1;

            historyList.add(h);
        }

        // The Image will sometimes stay the same, even if the numbers change.
        // This is because the browser will cache the generated image and might not notice that it has changed after reloading.
        // We cannot do anything about this (without JS and force reload)!
        String url = Gnuplotter.createStatisticsGraph(historyList);
        out.println("<img src=\"" + url + "\" alt=\"Graph\"> ");

        out.println("</body>");
        out.println("</html>");
    }
}
