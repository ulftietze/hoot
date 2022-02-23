package hoot.model.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class FollowerIDs
{
    public final User user;

    public ArrayList<Integer> followerIDs;

    public final LocalDateTime created;

    public FollowerIDs(User user, LocalDateTime created)
    {
        this.user    = user;
        this.created = created;
    }
}
