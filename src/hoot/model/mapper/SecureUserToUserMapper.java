package hoot.model.mapper;

import hoot.model.entities.User;
import hoot.model.entities.authentication.SecureUser;
import hoot.model.repositories.UserRepository;
import hoot.system.Exception.EntityNotFoundException;
import hoot.system.Filesystem.MediaFileHandler;
import hoot.system.ObjectManager.ObjectManager;
import hoot.system.Security.Hasher;

import java.security.GeneralSecurityException;

public class SecureUserToUserMapper
{
    private final UserRepository userRepository;

    private final Hasher hasher;

    private final MediaFileHandler mediaFileHandler;

    public SecureUserToUserMapper()
    {
        this.userRepository   = (UserRepository) ObjectManager.get(UserRepository.class);
        this.hasher           = (Hasher) ObjectManager.get(Hasher.class);
        this.mediaFileHandler = (MediaFileHandler) ObjectManager.get(MediaFileHandler.class);
    }

    public User map(SecureUser secureUser) throws EntityNotFoundException, GeneralSecurityException
    {
        User user = this.userRepository.getById(secureUser.id);

        user.username = secureUser.username;

        if (secureUser.password != null) {
            user.passwordHash = this.hasher.hash(secureUser.password);
        }

        if (secureUser.imageFilename != null && secureUser.image != null) {
            String imageName    = secureUser.imageFilename.substring(secureUser.imageFilename.lastIndexOf("/") + 1);
            String relativePath = secureUser.imageFilename.substring(secureUser.imageFilename.lastIndexOf("/"));
            this.mediaFileHandler.saveMedia(imageName, relativePath, secureUser.image);
            user.imagePath = relativePath;
        }

        return user;
    }
}
