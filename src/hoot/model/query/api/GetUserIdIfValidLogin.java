package hoot.model.query.api;

import hoot.model.entities.User;
import hoot.model.entities.authentication.Login;
import hoot.model.repositories.UserRepository;
import hoot.system.Exception.EntityNotFoundException;
import hoot.system.Logger.LoggerInterface;
import hoot.system.ObjectManager.ObjectManager;
import hoot.system.Security.Hasher;

import java.security.GeneralSecurityException;
import java.util.Objects;

public class GetUserIdIfValidLogin
{
    private final UserRepository userRepository;

    private final Hasher hasher;

    LoggerInterface logger;

    public GetUserIdIfValidLogin()
    {
        this.userRepository = (UserRepository) ObjectManager.get(UserRepository.class);
        this.hasher         = (Hasher) ObjectManager.get(Hasher.class);
        this.logger         = (LoggerInterface) ObjectManager.get(LoggerInterface.class);
    }

    /**
     * // TODO: Documentation
     *
     * @param login The LoginDTO which is used to transport data via the API
     * @return The UserId if the login data matches the database data
     */
    public User execute(Login login)
    {
        try {
            User user = this.userRepository.getByUsername(login.username);

            if (!Objects.equals(user.passwordHash, this.hasher.hash(login.password))) {
                return null;
            }

            return user;
        } catch (EntityNotFoundException e) {
            return null;
        } catch (GeneralSecurityException e) {
            this.logger.log("Unable to Login User. " + e.getMessage());
            return null;
        }
    }
}
