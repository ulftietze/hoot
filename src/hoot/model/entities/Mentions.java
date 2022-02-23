package hoot.model.entities;

import java.util.ArrayList;

public class Mentions
{
    public final Hoot hoot;

    public ArrayList<User> mentions;

    public Mentions(Hoot hoot)
    {
        this.hoot = hoot;
    }
}
