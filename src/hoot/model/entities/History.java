package hoot.model.entities;

import hoot.system.Monitoring.CollectorResult;
import hoot.system.objects.ObjectManager;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class History
{
    public Long id;

    public LocalDateTime timestamp;

    public Integer currentLoggedIn = 0, loginsPerSixHours = 0, threadCount = 0, requestsPerSecond = 0;

    public Long currentlyRegisteredUsers = 0L, registrationsPerSixHours = 0L;

    public Long threadCountTotal = 0L;

    public Float postsPerMinute = 0f, requestsLoggedInPerSecond = 0f;

    public Double systemLoadAverage = 0d, systemCPULoad = 0d, processCPULoad = 0d;

    public ArrayList<Tag> trendingHashtags;

    public CollectorResult workload = new CollectorResult();

    public CollectorResult queueSize = new CollectorResult();

    public CollectorResult cacheSize = new CollectorResult();

    public CollectorResult requestDurations = new CollectorResult();

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
