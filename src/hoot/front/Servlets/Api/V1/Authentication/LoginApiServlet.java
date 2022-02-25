package hoot.front.Servlets.Api.V1.Authentication;

import hoot.front.Servlets.Api.V1.AbstractApiServlet;
import hoot.front.api.dto.authentication.LoginDTO;
import hoot.model.entities.User;
import hoot.model.query.api.GetUserIdIfValidLogin;
import hoot.model.repositories.UserRepository;
import hoot.system.Exception.CouldNotSaveException;
import hoot.system.ObjectManager.ObjectManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;

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
        User                  user             = getUserIdIfValid.execute(login);

        session.setAttribute(LoginApiServlet.SESSION_USER_IDENTIFIER, null);

        // TODO: If already logged in this may be irrelevant
        if (user != null) {
            loggedIn = this.login(session, login, user);
        }

        String responseBody = this.serializeJsonResponseBody(loggedIn ? "success" : "failure");
        this.sendResponse(response, HttpServletResponse.SC_OK, responseBody);
    }

    private boolean login(HttpSession session, LoginDTO login, User user)
    {
        UserRepository repository = (UserRepository) ObjectManager.get(UserRepository.class);

        try {
            user.lastLogin = LocalDateTime.now();
            repository.save(user);
            session.setAttribute(LoginApiServlet.SESSION_USER_IDENTIFIER, user.id);
        } catch (CouldNotSaveException ignore) {
            return false;
        }

        return true;
    }
}
