package hoot.model.entities;

import java.io.Serializable;
import java.time.LocalDateTime;

public class User implements Serializable
{
    // id should never be changed
    public final int id;

    // allow modification for these columns, save changes to Database via DatabaseMapper
    public String username, imagePath, passwordHash;

    // these columns are set by the database, not by the application
    public final LocalDateTime lastLogin, created;

    public User(int id, LocalDateTime lastLogin, LocalDateTime created)
    {
        this.id        = id;
        this.lastLogin = lastLogin;
        this.created   = created;
    }
}