package hoot.front.Servlets.Api.V1.Authentication;

import hoot.front.Servlets.Api.V1.AbstractApiServlet;
import hoot.model.entities.User;
import hoot.model.entities.authentication.Login;
import hoot.model.query.api.GetUserIdIfValidLogin;
import hoot.model.query.api.IsValidUserSession;
import hoot.model.repositories.UserRepository;
import hoot.system.Exception.CouldNotSaveException;
import hoot.system.ObjectManager.ObjectManager;
import hoot.system.Queue.QueueManager;

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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        Login       login    = (Login) this.deserializeJsonRequestBody(request, Login.class);
        HttpSession session  = request.getSession(true);
        boolean     loggedIn = false;

        GetUserIdIfValidLogin getUserIdIfValid = (GetUserIdIfValidLogin) ObjectManager.get(GetUserIdIfValidLogin.class);
        User                  user             = getUserIdIfValid.execute(login);

        session.setAttribute(IsValidUserSession.SESSION_USER_IDENTIFIER, null);

        // TODO: If already logged in this may be irrelevant
        if (user != null) {
            loggedIn = this.login(session, login, user);
        }

        String responseBody = this.serialize(loggedIn ? "success" : "failure");
        this.sendResponse(response, HttpServletResponse.SC_OK, responseBody);
    }

    private boolean login(HttpSession session, Login login, User user)
    {
        UserRepository repository   = (UserRepository) ObjectManager.get(UserRepository.class);
        QueueManager   queueManager = (QueueManager) ObjectManager.get(QueueManager.class);

        try {
            user.lastLogin = LocalDateTime.now();
            repository.save(user);
            session.setAttribute(IsValidUserSession.SESSION_USER_IDENTIFIER, user.id);
            queueManager.add("login", user);
        } catch (CouldNotSaveException ignore) {
            return false;
        }

        return true;
    }
}
