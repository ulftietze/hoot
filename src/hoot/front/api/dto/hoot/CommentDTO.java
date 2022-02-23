package hoot.front.api.dto.hoot;

public class CommentDTO extends HootDTO
{
    public HootType type = HootType.comment;

    public HootDTO parent;

    public String content;
}
