package hoot.model.mapper.dtoToEntity.hoot;

import hoot.front.api.dto.hoot.*;
import hoot.model.entities.Hoot;
import hoot.system.Exception.CouldNotMapException;
import hoot.system.ObjectManager.ObjectManager;

public class HootDtoToHootMapper
{
    public Hoot map(HootDTO hootDTO) throws CouldNotMapException
    {
        Hoot hoot;

        switch (HootType.valueOf(String.valueOf(hootDTO.type))) {
            case post:
                hoot = this.getPostDtoToPostMapper().map((PostDTO) hootDTO);
                break;
            case image:
                hoot = this.getImageDtoToImageMapper().map((ImageDTO) hootDTO);
                break;
            case comment:
                hoot = this.getCommentDtoToCommentMapper().map((CommentDTO) hootDTO);
                break;
            default:
                throw new CouldNotMapException("Could not map: " + hootDTO.type.toString());
        }

        hoot.id      = hootDTO.id;
        hoot.created = hootDTO.created;

        return hoot;
    }

    private PostDtoToPostMapper getPostDtoToPostMapper()
    {
        return (PostDtoToPostMapper) ObjectManager.get(PostDtoToPostMapper.class);
    }

    private ImageDtoToImageMapper getImageDtoToImageMapper()
    {
        return (ImageDtoToImageMapper) ObjectManager.get(ImageDtoToImageMapper.class);
    }

    private CommentDtoToCommentMapper getCommentDtoToCommentMapper()
    {
        return (CommentDtoToCommentMapper) ObjectManager.get(CommentDtoToCommentMapper.class);
    }
}
