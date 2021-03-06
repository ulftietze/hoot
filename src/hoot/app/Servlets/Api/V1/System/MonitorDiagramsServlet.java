package hoot.app.Servlets.Api.V1.System;

import hoot.app.Servlets.Api.V1.AbstractApiServlet;
import hoot.model.monitoring.Gnuplotter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/V1/system/monitor/diagrams")
public class MonitorDiagramsServlet extends AbstractApiServlet
{
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        HashMap<String, Map<Gnuplotter.GraphType, String>> gnuplotter = new HashMap<>();
        gnuplotter.put("graphList", Gnuplotter.getGraphUrls());

        this.sendResponse(response, HttpServletResponse.SC_OK, this.serialize(gnuplotter));
    }
}
