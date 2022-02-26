package hoot.model.entities;

import java.time.LocalDateTime;

public class User
{
    public Integer id, followerCount, followsCount;

    public String username;

    public transient String imagePath, passwordHash;

    public LocalDateTime lastLogin, created;

    // TODO: Expose ImageUrl
    //@ExposeMethod("imageUrl")
    public String getImageUrl()
    {
        return "https://.../media/" + this.imagePath;
    }

    public void setPassword(String password)
    {
        this.passwordHash = "abvcd" + password + "sdfds";
    }
}