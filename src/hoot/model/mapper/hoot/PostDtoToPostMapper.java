package hoot.model.mapper.hoot;

import hoot.front.api.dto.hoot.PostDTO;
import hoot.model.entities.Hoot;
import hoot.model.entities.Post;

public class PostDtoToPostMapper
{
    public Hoot map(PostDTO hootDTO)
    {
        // TODO: Mapping
        return new Post();
    }
}
