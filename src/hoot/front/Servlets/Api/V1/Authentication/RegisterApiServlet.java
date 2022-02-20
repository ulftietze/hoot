package hoot.front.Servlets.Api.V1.Authentication;

import hoot.front.Servlets.Api.V1.AbstractApiServlet;
import hoot.front.api.dto.authentication.RegisterDTO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/V1/register")
public class RegisterApiServlet extends AbstractApiServlet
{
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        RegisterDTO register = (RegisterDTO) this.deserializeJsonRequestBody(request, RegisterDTO.class);

        response.setContentType("text/text");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        out.println(this.serializeJsonResponseBody(register));
    }
}
