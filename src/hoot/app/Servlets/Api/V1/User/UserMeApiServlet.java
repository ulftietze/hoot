package hoot.app.Servlets.Api.V1.User;

import hoot.app.Servlets.Api.V1.AbstractApiServlet;
import hoot.model.entities.User;
import hoot.model.entities.authentication.SecureUser;
import hoot.model.query.api.IsValidUserSession;
import hoot.model.repositories.UserRepository;
import hoot.system.Exception.CouldNotSaveException;
import hoot.system.Exception.EntityNotFoundException;
import hoot.system.Security.AuthenticationRequired;
import hoot.system.Security.Hasher;
import hoot.system.objects.ObjectManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;

@AuthenticationRequired
@WebServlet({"/api/V1/user/me"})
public class UserMeApiServlet extends AbstractApiServlet
{
    private UserRepository userRepository;

    @Override
    public void init() throws ServletException
    {
        super.init();

        this.userRepository = (UserRepository) ObjectManager.get(UserRepository.class);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        Integer currentUserId = (Integer) request
                .getSession(true)
                .getAttribute(IsValidUserSession.SESSION_USER_IDENTIFIER);

        try {
            User entity = this.userRepository.getById(currentUserId);

            this.sendResponse(response, HttpServletResponse.SC_OK, this.serialize(entity));
        } catch (EntityNotFoundException e) {
            int httpStatus = HttpServletResponse.SC_NOT_FOUND;
            this.sendResponse(response, httpStatus, this.serialize(e.getMessage()));
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        SecureUser secureUser = (SecureUser) this.deserializeJsonRequestBody(request, SecureUser.class);

        try {
            User entity = this.map(secureUser);
            this.userRepository.save(entity);

            this.sendResponse(response, HttpServletResponse.SC_OK, this.serialize("saved"));
        } catch (CouldNotSaveException | EntityNotFoundException | GeneralSecurityException e) {
            int httpStatus = HttpServletResponse.SC_NOT_ACCEPTABLE;
            this.sendResponse(response, httpStatus, this.serialize(e.getMessage()));
        }
    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        SecureUser secureUser = (SecureUser) this.deserializeJsonRequestBody(request, SecureUser.class);

        try {
            User entity = this.map(secureUser);
            this.userRepository.save(entity);

            this.sendResponse(response, HttpServletResponse.SC_OK, this.serialize("saved"));
        } catch (CouldNotSaveException | EntityNotFoundException | GeneralSecurityException e) {
            int httpStatus = HttpServletResponse.SC_NOT_ACCEPTABLE;
            this.sendResponse(response, httpStatus, this.serialize(e.getMessage()));
        }
    }

    private User map(SecureUser secureUser) throws EntityNotFoundException, GeneralSecurityException
    {
        UserRepository repository = (UserRepository) ObjectManager.get(UserRepository.class);
        Hasher         hasher     = (Hasher) ObjectManager.get(Hasher.class);
        User           user       = repository.getById(secureUser.id);

        user.username = secureUser.username;

        if (secureUser.password != null) {
            user.passwordHash = hasher.hash(secureUser.password);
        }

        this.saveImage(secureUser.imageFilename, secureUser.image);

        return user;
    }
}
