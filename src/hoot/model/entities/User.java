package hoot.model.entities;

import java.time.LocalDateTime;

public class User
{
    public Integer id;

    public String username, imagePath, passwordHash;

    public LocalDateTime lastLogin, created;
}