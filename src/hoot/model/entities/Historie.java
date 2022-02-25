package hoot.model.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Historie
{
    public long id;

    public LocalDateTime timestamp;

    public int currentLoggedIn, currentlyRegisteredUsers;

    public float postsPerSecond, requestsPerSecond, loginsPerSecond;

    public ArrayList<Tag> trendingHashtags;
}
