package hoot.front.Servlets;

import hoot.model.entities.History;
import hoot.model.monitoring.Gnuplotter;
import hoot.model.repositories.HistoryRepository;
import hoot.model.search.hoot.HistorySearchCriteria;
import hoot.system.Annotation.AuthenticationRequired;
import hoot.system.Exception.EntityNotFoundException;
import hoot.system.ObjectManager.ObjectManager;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

@AuthenticationRequired(authenticationRequired = false)
@WebServlet("/test")
public class TestServlet extends HttpServlet
{
    private HashMap<Gnuplotter.GraphType, LocalDateTime> generatedGraphMap = new HashMap<>();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        response.setContentType("text/html");

        PrintWriter out = response.getWriter();
        out.println("<!doctype html><html>");
        out.println("<head> <meta charset='utf-8'>");
        out.println("<title>JDBC Test</title> </head>");
        out.println("<body>");

        HistoryRepository historyRepository = (HistoryRepository) ObjectManager.get(HistoryRepository.class);

        HistorySearchCriteria historySearchCriteria = (HistorySearchCriteria) ObjectManager.get(HistorySearchCriteria.class);

        ArrayList<History> historyList = null;
        
        try {
            historyList = historyRepository.getList(historySearchCriteria);
        } catch (EntityNotFoundException e) {
            out.println("Could not get List of History Objects");
        }

        // The Image will sometimes stay the same, even if the numbers change.
        // This is because the browser will cache the generated image and might not notice that it has changed after reloading.
        // We cannot do anything about this (without JS and force reload)!

        if (historyList != null) {
            for (Gnuplotter.GraphType graphType : Gnuplotter.GraphType.values()) {
                String url = Gnuplotter.getGraphUrl(graphType);
                if (url == null || this.generatedGraphMap.get(graphType).isBefore(LocalDateTime.now().minusMinutes(1L))) {
                    url = Gnuplotter.createGraph(graphType, historyList);
                    this.generatedGraphMap.put(graphType, LocalDateTime.now());
                }
                out.println("<img src=\"" + url + "\" alt=\"Graph\"> ");
            }
        }

        out.println("</body>");
        out.println("</html>");
    }
}
