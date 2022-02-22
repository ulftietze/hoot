package hoot.front.Servlets.Api.V1.Authentication;

import hoot.front.Servlets.Api.V1.AbstractApiServlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/api/V1/logout")
public class LogoutApiServlet extends AbstractApiServlet
{
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        request.getSession().invalidate();

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
    }
}
