package hoot.model.mapper.entityToDto;

import hoot.front.api.dto.user.UserDTO;
import hoot.model.entities.User;
import hoot.system.Filesystem.MediaFileHandler;
import hoot.system.ObjectManager.ObjectManager;

public class UserToUserDtoMapper
{
    private final MediaFileHandler mediaFileHandler;

    public UserToUserDtoMapper()
    {
        this.mediaFileHandler = (MediaFileHandler) ObjectManager.get(MediaFileHandler.class);
    }

    public UserDTO map(User user)
    {
        UserDTO userDTO = new UserDTO();

        userDTO.id       = user.id;
        userDTO.username = user.username;

        return userDTO;
    }
}
