package hoot.front.Servlets.Api.V1.Authentication;

import hoot.front.Servlets.Api.V1.AbstractApiServlet;
import hoot.model.entities.User;
import hoot.model.entities.authentication.Registration;
import hoot.model.mapper.RegistrationToUserMapper;
import hoot.model.repositories.UserRepository;
import hoot.system.Exception.CouldNotSaveException;
import hoot.system.ObjectManager.ObjectManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;

@WebServlet("/api/V1/register")
public class RegisterApiServlet extends AbstractApiServlet
{
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        Registration register = (Registration) this.deserializeJsonRequestBody(request, Registration.class);

        RegistrationToUserMapper mapper     = (RegistrationToUserMapper) ObjectManager.get(RegistrationToUserMapper.class);
        UserRepository           repository = (UserRepository) ObjectManager.get(UserRepository.class);

        try {
            User user = mapper.map(register);
            repository.save(user);

            this.sendResponse(response, HttpServletResponse.SC_CREATED, this.serialize("Registered"));
        } catch (GeneralSecurityException | CouldNotSaveException e) {
            int httpStatus = HttpServletResponse.SC_CONFLICT;
            this.sendResponse(response, httpStatus, this.serialize(e.getMessage()));
        }
    }
}
