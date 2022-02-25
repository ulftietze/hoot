package hoot.model.mapper.hoot;

import hoot.front.api.dto.hoot.HootDTO;
import hoot.model.entities.Comment;
import hoot.model.entities.Hoot;

public class CommentDtoToCommentMapper
{
    public Hoot map(HootDTO hootDTO)
    {
        // TODO: Mapping
        return new Comment();
    }
}
