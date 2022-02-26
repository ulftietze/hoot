package hoot.model.mapper.entityToDto.hoot;

import hoot.front.api.dto.hoot.PostDTO;
import hoot.model.entities.Post;

public class PostToPostDtoMapper
{
    public PostDTO map(Post post)
    {
        PostDTO dto = new PostDTO();
        dto.content = post.content;

        return dto;
    }
}
