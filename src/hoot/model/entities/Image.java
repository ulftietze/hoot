package hoot.model.entities;

public class Image extends Hoot
{
    public transient String imagePath;

    public String content;

    public boolean onlyFollower;

    public Image()
    {
        this.hootType = HootType.Image;
    }

    public String getImageUrl()
    {
        return "https://....de/../" + this.imagePath;
    }
}
