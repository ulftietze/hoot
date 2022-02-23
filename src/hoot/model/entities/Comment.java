package hoot.model.entities;

import java.time.LocalDateTime;

public class Comment extends Hoot
{
    public Hoot parent;

    public String content;

    public Comment()
    {
        super();
    }

    public Comment(int id, User user, HootType hootType, LocalDateTime created, Hoot parent)
    {
        super(id, user, hootType, created);
        this.parent = parent;
    }
}
