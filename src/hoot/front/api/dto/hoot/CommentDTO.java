package hoot.front.api.dto.hoot;

public class CommentDTO extends HootDTO
{
    public HootDTO parent;

    public String content;

    public CommentDTO()
    {
        this.type = HootType.comment;
    }
}
