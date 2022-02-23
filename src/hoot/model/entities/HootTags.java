package hoot.model.entities;

import java.util.ArrayList;

public class HootTags
{
    public final Hoot hoot;

    public ArrayList<Tag> tags;

    public HootTags(Hoot hoot)
    {
        this.hoot = hoot;
    }
}
