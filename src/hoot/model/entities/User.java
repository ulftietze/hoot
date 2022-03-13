package hoot.model.entities;

import hoot.system.Filesystem.MediaFileHandler;
import hoot.system.ObjectManager.ObjectManager;

import java.io.Serializable;
import java.time.LocalDateTime;

public class User implements Serializable
{
    private final MediaFileHandler mediaFileHandler;
    public        Integer          id, followerCount, followsCount;
    public           String username;
    public transient String imagePath, passwordHash;
    public LocalDateTime lastLogin, created;

    public User()
    {
        this.mediaFileHandler = (MediaFileHandler) ObjectManager.get(MediaFileHandler.class);
    }

    public String getImageUrl()
    {
        String imagePath = "user/default.png";

        if (this.imagePath != null) {
            imagePath = this.imagePath;
        }

        return this.mediaFileHandler.getImageUrl(imagePath);
    }
}