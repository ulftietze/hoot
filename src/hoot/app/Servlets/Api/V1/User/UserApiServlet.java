package hoot.app.Servlets.Api.V1.User;

import hoot.app.Servlets.Api.V1.AbstractApiServlet;
import hoot.model.entities.User;
import hoot.model.entities.authentication.SecureUser;
import hoot.model.repositories.UserRepository;
import hoot.system.Security.AuthenticationRequired;
import hoot.system.Exception.CouldNotSaveException;
import hoot.system.Exception.EntityNotFoundException;
import hoot.system.objects.ObjectManager;
import hoot.system.Security.Hasher;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;

@AuthenticationRequired
@WebServlet({"/api/V1/user", "/api/V1/user/me"})
public class UserApiServlet extends AbstractApiServlet
{
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        String requestedId = request.getParameter("id");

        if (requestedId == null || requestedId.equals("")) {
            int httpStatus = HttpServletResponse.SC_BAD_REQUEST;
            this.sendResponse(response, httpStatus, this.serialize("No ID given."));
            return;
        }

        UserRepository repository = (UserRepository) ObjectManager.get(UserRepository.class);

        try {
            User entity = repository.getById(Integer.parseInt(requestedId));

            this.sendResponse(response, HttpServletResponse.SC_OK, this.serialize(entity));
        } catch (EntityNotFoundException e) {
            int httpStatus = HttpServletResponse.SC_NOT_FOUND;
            this.sendResponse(response, httpStatus, this.serialize(e.getMessage()));
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        UserRepository repository = (UserRepository) ObjectManager.get(UserRepository.class);
        SecureUser     secureUser = (SecureUser) this.deserializeJsonRequestBody(request, SecureUser.class);

        try {
            User entity = this.map(secureUser);
            repository.save(entity);

            this.sendResponse(response, HttpServletResponse.SC_OK, this.serialize("saved"));
        } catch (CouldNotSaveException | EntityNotFoundException | GeneralSecurityException e) {
            int httpStatus = HttpServletResponse.SC_NOT_ACCEPTABLE;
            this.sendResponse(response, httpStatus, this.serialize(e.getMessage()));
        }
    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        UserRepository repository = (UserRepository) ObjectManager.get(UserRepository.class);
        SecureUser     secureUser = (SecureUser) this.deserializeJsonRequestBody(request, SecureUser.class);

        try {
            User entity = this.map(secureUser);
            repository.save(entity);

            this.sendResponse(response, HttpServletResponse.SC_OK, this.serialize("saved"));
        } catch (CouldNotSaveException | EntityNotFoundException | GeneralSecurityException e) {
            int httpStatus = HttpServletResponse.SC_NOT_ACCEPTABLE;
            this.sendResponse(response, httpStatus, this.serialize(e.getMessage()));
        }
    }

    public User map(SecureUser secureUser) throws EntityNotFoundException, GeneralSecurityException
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
