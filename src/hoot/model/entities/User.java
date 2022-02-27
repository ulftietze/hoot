package hoot.model.entities;

import java.io.Serializable;
import java.time.LocalDateTime;

public class User implements Serializable
{
    public Integer id, followerCount, followsCount;

    public String username;

    public transient String imagePath, passwordHash;

    public LocalDateTime lastLogin, created;

    public String getImageUrl()
    {
        return "https://.../media/" + this.imagePath;
    }

    public void setPassword(String password)
    {
        this.passwordHash = "abvcd" + password + "sdfds";
    }
}