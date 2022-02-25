package hoot.model.entities;

import java.time.LocalDateTime;

public abstract class Hoot
{
    public int id;

    public User user;

    public HootType hootType;

    public LocalDateTime created;
}
