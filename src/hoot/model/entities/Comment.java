package hoot.model.entities;

public class Comment extends Hoot
{
    public Hoot parent;

    public String content;

    public Comment()
    {
        this.hootType = HootType.Comment;
    }
}
