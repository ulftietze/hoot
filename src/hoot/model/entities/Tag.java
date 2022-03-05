package hoot.model.entities;

public class Tag implements Comparable<Tag>
{
    public String tag;

    @Override
    public int compareTo(Tag tag)
    {
        return this.tag.compareTo(tag.tag);
    }
}
