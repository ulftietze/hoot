package hoot.model.entities;

import java.time.LocalDateTime;

public class Hoot
{
    // id should never be changed
    public int id;

    // changing the user of a hoot does not make sense
    public User user;

    // hootType will always be the same (comment cannot magically become a image)
    public HootType hootType;

    // set by the DB
    public LocalDateTime created;

    public Hoot()
    {}

    public Hoot(int id, User user, HootType hootType, LocalDateTime created)
    {
        this.id       = id;
        this.user     = user;
        this.hootType = hootType;
        this.created  = created;
    }
}
