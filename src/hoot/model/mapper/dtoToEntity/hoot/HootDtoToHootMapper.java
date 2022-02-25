package hoot.model.mapper.dtoToEntity.hoot;

import hoot.front.api.dto.hoot.*;
import hoot.model.entities.Hoot;
import hoot.system.Exception.CouldNotMapException;
import hoot.system.ObjectManager.ObjectManager;

public class HootDtoToHootMapper
{
    public Hoot map(HootDTO hootDTO) throws CouldNotMapException
    {
        Hoot                hoot;
        PostDtoToPostMapper postMapper = (PostDtoToPostMapper) ObjectManager.get(PostDtoToPostMapper.class);
        ImageDtoToImageMapper imageMapper = (ImageDtoToImageMapper) ObjectManager.get(ImageDtoToImageMapper.class);
        CommentDtoToCommentMapper commentMapper = (CommentDtoToCommentMapper) ObjectManager.get(CommentDtoToCommentMapper.class);

        switch (HootType.valueOf(String.valueOf(hootDTO.type))) {
            case post:
                hoot = postMapper.map((PostDTO) hootDTO);
                break;
            case image:
                hoot = imageMapper.map((ImageDTO) hootDTO);
                break;
            case comment:
                hoot = commentMapper.map((CommentDTO) hootDTO);
                break;
            default:
                throw new CouldNotMapException("Invalid Type "+ hootDTO.type.toString());
        }
        hoot.id = hootDTO.id;
        hoot.created = hootDTO.created;
        return hoot;
    }
}
