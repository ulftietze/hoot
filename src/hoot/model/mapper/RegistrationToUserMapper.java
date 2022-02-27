package hoot.model.mapper;

import hoot.model.entities.User;
import hoot.model.entities.authentication.Registration;
import hoot.system.Filesystem.MediaFileHandler;
import hoot.system.ObjectManager.ObjectManager;
import hoot.system.Security.Hasher;

import java.security.GeneralSecurityException;

public class RegistrationToUserMapper
{
    private final Hasher hasher;

    private final MediaFileHandler mediaFileHandler;

    public RegistrationToUserMapper()
    {
        this.hasher           = (Hasher) ObjectManager.get(Hasher.class);
        this.mediaFileHandler = (MediaFileHandler) ObjectManager.get(MediaFileHandler.class);
    }

    public User map(Registration register) throws GeneralSecurityException
    {
        User user = new User();
        this.saveImageIfProvided(register);

        user.username     = register.username;
        user.passwordHash = this.hasher.hash(register.password);
        user.imagePath    = register.imageFilename;

        return user;
    }

    private void saveImageIfProvided(Registration register)
    {
        if (register.image == null || register.image.equals("") || register.imageFilename == null
            || register.imageFilename.equals("")) {
            return;
        }

        String imageName    = register.imageFilename.substring(register.imageFilename.lastIndexOf("/") + 1);
        String relativePath = register.imageFilename.substring(register.imageFilename.lastIndexOf("/"));

        this.mediaFileHandler.saveMedia(imageName, relativePath, register.image);
    }
}
