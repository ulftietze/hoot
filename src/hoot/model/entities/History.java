package hoot.model.entities;

import hoot.system.ObjectManager.ObjectManager;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class History
{
    public Long id;

    public LocalDateTime timestamp;

    public Integer currentLoggedIn   = 0,
            currentlyRegisteredUsers = 0,
            memoryMax                = 0,
            memoryTotal              = 0,
            memoryFree               = 0,
            memoryUsed               = 0,
            threadCount              = 0,
            threadCountTotal         = 0;

    public Float loginsPerSixHours    = 0f,
            registrationsPerSixHours  = 0f,
            postsPerMinute            = 0f,
            requestsPerSecond         = 0f,
            requestsLoggedInPerSecond = 0f;

    public Double systemLoadAverage = 0d,
            systemCPULoad           = 0d,
            processCPULoad          = 0d;

    public ArrayList<Tag> trendingHashtags;

    public static ArrayList<Tag> getTrendingHashtagsFromCommaSeparatedTags(String commaSeparatedTags)
    {
        ArrayList<Tag> tagList = new ArrayList<>();

        String[] splitTags = commaSeparatedTags.stripLeading().split(",");

        for (String stringTag : splitTags) {
            Tag tag = (Tag) ObjectManager.create(Tag.class);
            tag.tag = stringTag;
            tagList.add(tag);
        }

        return tagList;
    }

    public String getCommaSeperatedTags()
    {
        if (trendingHashtags == null || trendingHashtags.size() == 0) {
            return "";
        }

        StringBuilder builder = (StringBuilder) ObjectManager.create(StringBuilder.class);

        builder.append(trendingHashtags.get(0).tag);

        for (int i = 1; i < trendingHashtags.size(); ++i) {
            builder.append(",").append(trendingHashtags.get(i).tag);
        }

        return builder.toString();
    }
}
