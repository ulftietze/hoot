package hoot.model.mapper.dtoToEntity.hoot;

import hoot.front.api.dto.hoot.PostDTO;
import hoot.model.entities.Hoot;
import hoot.model.entities.Post;

public class PostDtoToPostMapper
{
    public Hoot map(PostDTO postDTO)
    {
        Post post = new Post();

        post.content = postDTO.content;
        return new Post();
    }
}
