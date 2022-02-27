package hoot.model.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Historie
{
    public Long id;

    public LocalDateTime timestamp;

    public Integer currentLoggedIn, currentlyRegisteredUsers;

    public Float postsPerSecond, requestsPerSecond, loginsPerSecond;

    public ArrayList<Tag> trendingHashtags;
}
