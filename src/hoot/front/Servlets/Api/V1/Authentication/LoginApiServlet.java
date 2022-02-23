package hoot.front.Servlets.Api.V1.Authentication;

import hoot.front.Servlets.Api.V1.AbstractApiServlet;
import hoot.front.api.dto.authentication.LoginDTO;
import hoot.model.query.api.GetUserIdIfValidLogin;
import hoot.system.ObjectManager.ObjectManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/api/V1/login")
public class LoginApiServlet extends AbstractApiServlet
{
    public static String SESSION_USER_IDENTIFIER = "userId";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        LoginDTO    login    = (LoginDTO) this.deserializeJsonRequestBody(request, LoginDTO.class);
        HttpSession session  = request.getSession(true);
        boolean     loggedIn = false;

        GetUserIdIfValidLogin getUserIdIfValid = (GetUserIdIfValidLogin) ObjectManager.get(GetUserIdIfValidLogin.class);
        Integer               userId           = getUserIdIfValid.execute(login);

        session.setAttribute(LoginApiServlet.SESSION_USER_IDENTIFIER, null);

        // TODO: If already logged in this is irrelevant
        if (userId != null) {
            loggedIn = true;
            session.setAttribute(LoginApiServlet.SESSION_USER_IDENTIFIER, userId);
            // TODO: Update User.LastLoggedIn
        }

        String responseBody = this.serializeJsonResponseBody(loggedIn ? "success" : "failure");
        this.sendResponse(response, HttpServletResponse.SC_OK, responseBody);
    }
}
