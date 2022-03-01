package hoot.model.entities;

import hoot.system.ObjectManager.ObjectManager;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Historie
{
    public Long id;

    public LocalDateTime timestamp;

    public Integer currentLoggedIn, currentlyRegisteredUsers;

    public Float postsPerSecond, requestsPerSecond, loginsPerSecond;

    public ArrayList<Tag> trendingHashtags;

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

    public ArrayList<Tag> getTrendingHashtagsFromCommaSeparatedTags(String commaSeparatedTags)
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
}
