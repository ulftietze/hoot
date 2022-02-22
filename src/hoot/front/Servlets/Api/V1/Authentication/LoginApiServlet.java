package hoot.front.Servlets.Api.V1.Authentication;

import hoot.front.Servlets.Api.V1.AbstractApiServlet;
import hoot.front.api.dto.authentication.LoginDTO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Objects;

@WebServlet("/api/V1/login")
public class LoginApiServlet extends AbstractApiServlet
{
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        LoginDTO    login    = (LoginDTO) this.deserializeJsonRequestBody(request, LoginDTO.class);
        HttpSession session  = request.getSession(true);
        boolean     loggedIn = false;
        session.setAttribute("userId", null);

        // TODO: Correct Username/Password Check
        // TODO: If already logged in this is irrelevant
        if (Objects.equals(login.username, "t-usr") && Objects.equals(login.password, "t-pwd")) {
            loggedIn = true;
            session.setAttribute("userId", 1);
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        out.println(this.serializeJsonResponseBody(loggedIn ? "success" : "failure"));
    }
}
