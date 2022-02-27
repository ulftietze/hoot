package hoot.model.entities;

public class Post extends Hoot
{
    public String content;

    public boolean onlyFollower;

    public Post()
    {
        this.hootType = HootType.Post;
    }
}
