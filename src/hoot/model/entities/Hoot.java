package hoot.model.entities;

import java.time.LocalDateTime;

public class Hoot
{
    // id should never be changed
    public final int id;

    // changing the user of a hoot does not make sense
    public final User user;

    // hootType will always be the same (comment cannot magically become a image)
    public final HootType hootType;

    // set by the DB
    public final LocalDateTime created;

    public Hoot(int id, User user, HootType hootType, LocalDateTime created)
    {
        this.id       = id;
        this.user     = user;
        this.hootType = hootType;
        this.created  = created;
    }
}
