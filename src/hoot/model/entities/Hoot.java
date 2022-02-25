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

    public HootTags tags;

    public Map<Interaction, Integer> reactionCount = new HashMap<>();
}
