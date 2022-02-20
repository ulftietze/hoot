package hoot.front.Servlets;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet({"/openApi", "/swagger", "/open-api-specification"})
public class OpenApiDocumentationServlet extends HttpServlet
{
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        response.setContentType("text/html");

        PrintWriter out = response.getWriter();
        out.println("<!doctype html><html lang=\"en\">");
        out.println("<head>");
        out.println("<link "
                    + "rel=\"stylesheet\" "
                    + "href=\"https://cdn.jsdelivr.net/npm/swagger-ui-dist@4.5.0/swagger-ui.css\">");
        out.println("<link "
                    + "rel=\"stylesheet\" "
                    + "href=\"" + request.getContextPath() + "/assets/main.css\">");
        out.println("<script src=\"https://unpkg.com/swagger-ui-dist@4.5.0/swagger-ui-bundle.js\" crossorigin></script>");
        out.println("<script>\n"
                    + "    window.onload = () => {\n"
                    + "        window.ui = SwaggerUIBundle({\n"
                    + "            url: '" + request.getContextPath() + "/static/api/V1.yml',\n"
                    + "            dom_id: '#swagger-ui',\n"
                    + "        });\n"
                    + "    };\n"
                    + "</script>");
        out.println("<title>Api V1 Specification</title> </head>");
        out.println("<body></body>");
        out.println("<div id=\"swagger-ui\"></div>");
        out.println("</body>");
        out.println("</html>");

        ServletContext context = getServletContext();
        context.log("simple logging");
    }
}
