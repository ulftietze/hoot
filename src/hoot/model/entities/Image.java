package hoot.model.entities;

import java.time.LocalDateTime;

import static hoot.front.api.dto.hoot.HootType.image;

public class Image extends Hoot
{
    public String imagePath;

    public String content;

    public boolean onlyFollower;

    public Image()
    {
        this.hootType = new HootType();
        this.hootType.hootType = image.toString();
    }

    public Image(int id, User user, HootType hootType, LocalDateTime created, String imagePath)
    {
        super(id, user, hootType, created);
        this.imagePath = imagePath;
    }
}
