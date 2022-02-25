package hoot.model.entities;

import java.time.LocalDateTime;

public class Image extends Hoot
{
    public String imagePath;

    public String content;

    public boolean onlyFollower;

    public Image()
    {
        this.hootType = HootType.Image;
    }
}
