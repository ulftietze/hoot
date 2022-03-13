package hoot.model.entities;

import hoot.system.Filesystem.MediaFileHandler;
import hoot.system.ObjectManager.ObjectManager;

public class Image extends Hoot
{
    private final    MediaFileHandler mediaFileHandler;
    public transient String           imagePath;
    public           String           content;
    public           boolean          onlyFollower;

    public Image()
    {
        this.mediaFileHandler = (MediaFileHandler) ObjectManager.get(MediaFileHandler.class);
        this.hootType         = HootType.Image;
    }

    public String getImageUrl()
    {
        String imagePath = "default.png";

        if (this.imagePath != null) {
            imagePath = this.imagePath;
        }

        return this.mediaFileHandler.getImageUrl("hoots/image/" + imagePath);
    }
}
