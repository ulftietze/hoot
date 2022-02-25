package hoot.model.entities;

import java.time.LocalDateTime;

public class Post extends Hoot
{
    public String content;

    public boolean onlyFollower;

    public Post()
    {
        
    }

    public Post(int id, User user, HootType hootType, LocalDateTime created)
    {
        //super(id, user, hootType, created);
    }
}
