package hoot.model.entities;

import java.io.Serializable;
import java.time.LocalDateTime;

public class User implements Serializable
{
    public Integer id;
    public Long    followerCount, followsCount;
    public String username, imagePath;
    public transient String        passwordHash;
    public           LocalDateTime lastLogin, created;
}