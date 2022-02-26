package hoot.model.entities;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public abstract class Hoot
{
    public int id;

    public User user;

    public HootType hootType;

    public LocalDateTime created;

    public Mentions mentions;

    public HootTags hootTags;

    public Map<Interaction, Integer> reactionCount = new HashMap<>();

    public void addTag(String tag)
    {
        if (this.hootTags == null) {
            this.hootTags = new HootTags();
        }

        Tag t = new Tag();
        t.tag = tag;
        this.hootTags.tags.add(t);
    }
}
