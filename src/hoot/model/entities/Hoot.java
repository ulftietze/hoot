package hoot.model.entities;

import hoot.system.objects.ObjectManager;

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

    public Map<Interaction, Long> reactionCount = new HashMap<>();

    public Hoot()
    {
        this.mentions      = (HootMentions) ObjectManager.create(HootMentions.class);
        this.mentions.hoot = this;

        this.tags      = (HootTags) ObjectManager.create(HootTags.class);
        this.tags.hoot = this;
    }
}
