package hoot.model.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Historie
{
    public final long id;

    public final LocalDateTime timestamp;

    public int currentLoggedIn, currentlyRegisteredUsers;

    public float postsPerSecond, requestsPerSecond, loginsPerSecond;

    public ArrayList<Tag> trendingHashtags;

    public Historie(long id, LocalDateTime timestamp)
    {
        this.id        = id;
        this.timestamp = timestamp;
    }
}
