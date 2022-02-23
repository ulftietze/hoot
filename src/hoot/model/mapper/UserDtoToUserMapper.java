package hoot.model.mapper;

import hoot.front.api.dto.user.UserDTO;
import hoot.model.entities.User;
import hoot.system.Filesystem.MediaFileHandler;
import hoot.system.ObjectManager.ObjectManager;

import java.io.File;

public class UserDtoToUserMapper
{
    public static final String MEDIA_DIRECTIVE = "user";

    private final MediaFileHandler mediaFileHandler;

    public UserDtoToUserMapper()
    {
        this.mediaFileHandler = (MediaFileHandler) ObjectManager.get(MediaFileHandler.class);
    }

    public User map(UserDTO dto)
    {
        // TODO: Mapping
        User user = new User();

        String relativePath = MEDIA_DIRECTIVE + File.separator + dto.imageFilename;
        this.mediaFileHandler.saveMedia(dto.imageFilename, relativePath, dto.image);

        user.id           = dto.id;
        user.username     = dto.username;
        user.imagePath    = relativePath;
        user.passwordHash = user.passwordHash;

        return user;
    }
}
