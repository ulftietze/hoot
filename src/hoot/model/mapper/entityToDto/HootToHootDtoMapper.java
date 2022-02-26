package hoot.model.mapper.entityToDto;

import hoot.front.api.dto.hoot.HootDTO;
import hoot.model.entities.Comment;
import hoot.model.entities.Hoot;
import hoot.model.entities.Image;
import hoot.model.entities.Post;
import hoot.model.mapper.entityToDto.hoot.CommentToCommentDtoMapper;
import hoot.model.mapper.entityToDto.hoot.ImageToImageDtoMapper;
import hoot.model.mapper.entityToDto.hoot.PostToPostDtoMapper;
import hoot.system.Exception.CouldNotMapException;
import hoot.system.ObjectManager.ObjectManager;

public class HootToHootDtoMapper
{
    public HootDTO map(Hoot hoot) throws CouldNotMapException
    {
        HootDTO dto;

        switch (hoot.hootType) {
            case Post:
                dto = this.getPostToPostDtoMapper().map((Post) hoot);
                break;
            case Image:
                dto = this.getImageToImageDtoMapper().map((Image) hoot);
                break;
            case Comment:
                dto = this.getCommentToCommentDtoMapper().map((Comment) hoot);
                break;
            default:
                throw new CouldNotMapException("Could not map Entity.hootType to DTO: " + hoot.hootType.toString());
        }

        dto.id      = hoot.id;
        dto.created = hoot.created;
        dto.user    = this.getUserToUserDtoMapper().map(hoot.user);

        return dto;
    }

    private PostToPostDtoMapper getPostToPostDtoMapper()
    {
        return (PostToPostDtoMapper) ObjectManager.get(PostToPostDtoMapper.class);
    }

    private ImageToImageDtoMapper getImageToImageDtoMapper()
    {
        return (ImageToImageDtoMapper) ObjectManager.get(ImageToImageDtoMapper.class);
    }

    private CommentToCommentDtoMapper getCommentToCommentDtoMapper()
    {
        return (CommentToCommentDtoMapper) ObjectManager.get(CommentToCommentDtoMapper.class);
    }

    private UserToUserDtoMapper getUserToUserDtoMapper()
    {
        return (UserToUserDtoMapper) ObjectManager.get(UserToUserDtoMapper.class);
    }
}
