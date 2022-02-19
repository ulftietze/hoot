package hbv.Servlets.Api.V1.Authentication;

import hbv.Servlets.Api.V1.AbstractApiServlet;
import hbv.api.dto.authentication.LoginDTO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/V1/login")
public class LoginApiServlet extends AbstractApiServlet
{
    protected void post(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        LoginDTO login = (LoginDTO) this.deserializeJsonRequestBody(request, LoginDTO.class);

        response.setContentType("text/text");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        out.println(this.serializeJsonResponseBody(login));
    }
}
