package hoot.model.entities;

import java.time.LocalDateTime;

public class Comment extends Hoot
{
    public final Hoot parent;

    public String content;

    public Comment(int id, User user, HootType hootType, LocalDateTime created, Hoot parent)
    {
        super(id, user, hootType, created);
        this.parent = parent;
    }
}
