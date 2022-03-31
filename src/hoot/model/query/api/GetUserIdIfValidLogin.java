package hoot.model.query.api;

import hoot.model.entities.User;
import hoot.model.entities.authentication.Login;
import hoot.model.repositories.UserRepository;
import hoot.system.Exception.EntityNotFoundException;
import hoot.system.Logger.LoggerInterface;
import hoot.system.Security.Hasher;
import hoot.system.objects.Inject;

import java.security.GeneralSecurityException;
import java.util.Objects;

public class GetUserIdIfValidLogin
{
    @Inject private UserRepository userRepository;

    @Inject private Hasher hasher;

    @Inject private LoggerInterface logger;

    /**
     * // TODO: Documentation
     *
     * @param login The Login which is used to transport data via the API
     * @return The UserId if the login data matches the database data
     */
    public User execute(Login login)
    {
        try {
            return this.userRepository.getByUsernameAndPassword(login.username, this.hasher.hash(login.password));
        } catch (EntityNotFoundException e) {
            return null;
        } catch (GeneralSecurityException e) {
            this.logger.log("Unable to Login User. " + e.getMessage());
            return null;
        }
    }
}
