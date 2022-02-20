package hoot.front.Servlets.Api.V1.Hoots;

import hbv.Exceptions.UnauthorizedException;
import hoot.front.Servlets.Api.V1.AbstractApiServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/V1/hoots/comment")
public class CommentApiServlet extends AbstractApiServlet
{
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        try {
            this.validateAuthentication(request);
        } catch (UnauthorizedException e) {
            response.setStatus(401);
            PrintWriter out = response.getWriter();
            out.println(this.serializeJsonResponseBody("Unauthorized"));

            return;
        }

        response.setContentType("application/json");

        PrintWriter out = response.getWriter();
        out.println("<!doctype html><html>");
        out.println("<head> <meta charset='utf-8'>");
        out.println("<title>webapp</title> </head>");
        out.println("<body>CreateCommentServlet</body>");
        out.println("</html>");

        ServletContext context = getServletContext();
        context.log("simple logging");
    }
}
