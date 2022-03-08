package hoot.model.entities;

import java.util.Objects;

public class Tag implements Comparable<Tag>
{
    public String tag;

    @Override
    public int compareTo(Tag tag)
    {
        return this.tag.compareTo(tag.tag);
    }

    public boolean equals(Tag tag)
    {
        return tag != null && Objects.equals(this.tag, tag.tag);
    }
}
