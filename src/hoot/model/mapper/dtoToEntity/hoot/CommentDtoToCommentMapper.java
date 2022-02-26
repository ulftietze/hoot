package hoot.model.mapper.dtoToEntity.hoot;

import hoot.front.api.dto.hoot.CommentDTO;
import hoot.model.entities.Comment;
import hoot.model.entities.Hoot;
import hoot.system.Exception.CouldNotMapException;
import hoot.system.ObjectManager.ObjectManager;

import java.util.StringTokenizer;

public class CommentDtoToCommentMapper
{
    public Hoot map(CommentDTO commentDTO) throws CouldNotMapException
    {
        Comment             comment    = new Comment();
        HootDtoToHootMapper hootMapper = (HootDtoToHootMapper) ObjectManager.get(HootDtoToHootMapper.class);

        comment.parent  = hootMapper.map(commentDTO.parent);
        comment.content = commentDTO.content;

        StringTokenizer tokenizer = new StringTokenizer(commentDTO.content);
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (token.startsWith("#")) {
                comment.addTag(token);
            }
        }

        return comment;
    }
}
