package hoot.model.mapper.entityToDto.hoot;

import hoot.front.api.dto.hoot.CommentDTO;
import hoot.model.entities.Comment;

public class CommentToCommentDtoMapper
{
    public CommentDTO map(Comment comment)
    {
        return new CommentDTO();
    }
}
