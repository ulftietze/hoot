package hoot.app.Servlets.Api.V1.Authentication;

import hoot.app.Servlets.Api.V1.AbstractApiServlet;
import hoot.model.entities.User;
import hoot.model.entities.authentication.Registration;
import hoot.model.queue.publisher.RegistrationPublisher;
import hoot.model.repositories.UserRepository;
import hoot.system.Exception.CouldNotSaveException;
import hoot.system.ObjectManager.ObjectManager;
import hoot.system.Security.Hasher;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;

@WebServlet("/api/V1/register")
public class RegisterApiServlet extends AbstractApiServlet
{
    private RegistrationPublisher registrationPublisher;

    @Override
    public void init() throws ServletException
    {
        super.init();

        this.registrationPublisher = (RegistrationPublisher) ObjectManager.get(RegistrationPublisher.class);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        Registration register = (Registration) this.deserializeJsonRequestBody(request, Registration.class);

        UserRepository repository = (UserRepository) ObjectManager.get(UserRepository.class);
        Hasher         hasher     = (Hasher) ObjectManager.get(Hasher.class);

        try {
            User entity = (User) ObjectManager.create(User.class);
            entity.username     = register.username;
            entity.passwordHash = hasher.hash(register.password);
            this.saveImage(register.imageFilename, register.image);

            repository.save(entity);

            this.registrationPublisher.publish(entity);
            this.sendResponse(response, HttpServletResponse.SC_CREATED, this.serialize("Registered"));
        } catch (GeneralSecurityException | CouldNotSaveException e) {
            int httpStatus = HttpServletResponse.SC_CONFLICT;
            this.sendResponse(response, httpStatus, this.serialize(e.getMessage()));
        }
    }
}
