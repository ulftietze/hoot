package hoot.model.mapper;

import hoot.front.api.dto.authentication.RegisterDTO;
import hoot.model.entities.User;
import hoot.model.query.GetStringHashed;
import hoot.system.Filesystem.MediaFileHandler;
import hoot.system.ObjectManager.ObjectManager;

import java.security.GeneralSecurityException;

public class RegisterDtoToUserMapper
{
    private final GetStringHashed getStringHashed;

    private final MediaFileHandler mediaFileHandler;

    public RegisterDtoToUserMapper()
    {
        this.getStringHashed  = (GetStringHashed) ObjectManager.get(GetStringHashed.class);
        this.mediaFileHandler = (MediaFileHandler) ObjectManager.get(MediaFileHandler.class);
    }

    public User map(RegisterDTO register) throws GeneralSecurityException
    {
        User user = new User();
        this.saveImageIfProvided(register);

        user.username     = register.username;
        user.passwordHash = getStringHashed.execute(register.password);
        user.imagePath    = register.imageFilename;

        return user;
    }

    private void saveImageIfProvided(RegisterDTO register)
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
