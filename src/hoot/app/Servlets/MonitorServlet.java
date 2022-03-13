package hoot.app.Servlets;

import hoot.model.monitoring.Gnuplotter;
import hoot.system.Annotation.AuthenticationRequired;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.UUID;

@AuthenticationRequired(authenticationRequired = false)
@WebServlet("/monitor")
public class MonitorServlet extends HttpServlet
{
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        response.setContentType("text/html");

        PrintWriter out = response.getWriter();
        out.println("<!doctype html><html>");
        out.println("<head> <meta charset='utf-8'>");
        out.println("<title>Hoot Monitor</title> </head>");
        out.println("<body>");

        // The Image will sometimes stay the same, even if the numbers change.
        // This is because the browser will cache the generated image and might not notice that it has changed after reloading.
        // We cannot do anything about this (without JS and force reload)!

        for (Gnuplotter.GraphType graphType : Gnuplotter.GraphType.values()) {
            String url = Gnuplotter.getGraphUrl(graphType);
            out.println("<img src=\"" + url + "?" + UUID.randomUUID() + "\" alt=\"Graph\"> ");
        }

        out.println("</body>");
        out.println("</html>");
    }
}
