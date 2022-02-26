package hoot.front.Servlets.Api.V1.User;

import hoot.front.Servlets.Api.V1.AbstractApiServlet;
import hoot.model.entities.User;
import hoot.model.entities.authentication.SecureUser;
import hoot.model.mapper.SecureUserToUserMapper;
import hoot.model.repositories.UserRepository;
import hoot.system.Annotation.AuthenticationRequired;
import hoot.system.Exception.CouldNotSaveException;
import hoot.system.Exception.EntityNotFoundException;
import hoot.system.ObjectManager.ObjectManager;

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
        UserRepository         repository = (UserRepository) ObjectManager.get(UserRepository.class);
        SecureUserToUserMapper mapper     = (SecureUserToUserMapper) ObjectManager.get(SecureUserToUserMapper.class);

        SecureUser secureUser = (SecureUser) this.deserializeJsonRequestBody(request, SecureUser.class);

        try {
            User entity = mapper.map(secureUser);
            repository.save(entity);

            this.sendResponse(response, HttpServletResponse.SC_OK, this.serialize("saved"));
        } catch (CouldNotSaveException | EntityNotFoundException | GeneralSecurityException e) {
            int httpStatus = HttpServletResponse.SC_NOT_ACCEPTABLE;
            this.sendResponse(response, httpStatus, this.serialize(e.getMessage()));
        }
    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        UserRepository         repository = (UserRepository) ObjectManager.get(UserRepository.class);
        SecureUserToUserMapper mapper     = (SecureUserToUserMapper) ObjectManager.get(SecureUserToUserMapper.class);

        SecureUser secureUser = (SecureUser) this.deserializeJsonRequestBody(request, SecureUser.class);

        try {
            User entity = mapper.map(secureUser);
            repository.save(entity);

            this.sendResponse(response, HttpServletResponse.SC_OK, this.serialize("saved"));
        } catch (CouldNotSaveException | EntityNotFoundException | GeneralSecurityException e) {
            int httpStatus = HttpServletResponse.SC_NOT_ACCEPTABLE;
            this.sendResponse(response, httpStatus, this.serialize(e.getMessage()));
        }
    }
}
