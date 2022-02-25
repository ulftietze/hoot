package hoot.model.query.api;

import hoot.front.api.dto.authentication.LoginDTO;
import hoot.model.entities.User;
import hoot.model.repositories.UserRepository;
import hoot.system.Exception.EntityNotFoundException;
import hoot.system.Logger.LoggerInterface;
import hoot.system.ObjectManager.ObjectManager;
import hoot.system.Security.Encryptor;

import javax.sql.DataSource;
import java.security.GeneralSecurityException;
import java.util.Objects;

public class GetUserIdIfValidLogin
{
    private final UserRepository userRepository;

    private final DataSource dataSource;

    private final Encryptor encryptor;

    LoggerInterface logger;

    public GetUserIdIfValidLogin()
    {
        this.userRepository = (UserRepository) ObjectManager.get(UserRepository.class);
        this.dataSource     = (DataSource) ObjectManager.get(DataSource.class);
        this.encryptor      = (Encryptor) ObjectManager.get(Encryptor.class);
        this.logger         = (LoggerInterface) ObjectManager.get(LoggerInterface.class);
    }

    /**
     * // TODO: Return the User Entity
     *
     * @param login The LoginDTO which is used to transport data via the API
     * @return The UserId if the login data matches the database data
     */
    public User execute(LoginDTO login)
    {
        try {
            User user = this.userRepository.getByUsername(login.username);

            if (!Objects.equals(user.passwordHash, this.encryptor.hash(login.password))) {
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
