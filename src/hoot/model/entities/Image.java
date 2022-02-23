package hoot.model.entities;

import java.time.LocalDateTime;

public class Image extends Hoot
{
    public final String imagePath;

    public String content;

    public boolean onlyFollower;

    public Image(int id, User user, HootType hootType, LocalDateTime created, String imagePath)
    {
        super(id, user, hootType, created);
        this.imagePath = imagePath;
    }
}
