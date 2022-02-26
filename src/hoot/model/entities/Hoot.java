package hoot.model.entities;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public abstract class Hoot
{
    public Integer id;

    public User user;

    public HootType hootType;

    public LocalDateTime created;

    public HootMentions mentions;

    public HootTags tags;

    public Map<Interaction, Integer> reactionCount = new HashMap<>();

    public Hoot()
    {
        this.mentions = new HootMentions();
        this.mentions.hoot = this;

        this.tags = new HootTags();
        this.tags.hoot = this;
    }
}
