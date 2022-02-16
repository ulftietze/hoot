package hbv;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class HelloServlet extends HttpServlet {

  protected void doGet(HttpServletRequest  request, 
      HttpServletResponse response)
      throws IOException, ServletException {

      response.setContentType("text/html");
      PrintWriter out = response.getWriter();
      out.println("<!doctype html><html>");
      out.println("<head> <meta charset='utf-8'>");
      out.println("<title>webapp</title> </head>");
      out.println("<body>Hello</body>");
      out.println("</html>");
      ServletContext context = getServletContext();
      context.log("simple logging");
  }
}
