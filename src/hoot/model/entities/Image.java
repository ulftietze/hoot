package hoot.model.entities;

public class Image extends Hoot
{
    public String  imagePath;
    public String  content;
    public boolean onlyFollower;

    public Image()
    {
        this.hootType = HootType.Image;
    }
}
