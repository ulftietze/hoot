package hoot.model.entities;

public class Reaction
{
    // id should never be changed
    public final int id;

    // if the user wants to delete his reaction, the Reaction is deleted by its id
    public final User user;

    public final Hoot hoot;

    public Interaction interaction;

    public Reaction(int id, User user, Hoot hoot)
    {
        this.id   = id;
        this.user = user;
        this.hoot = hoot;
    }
}
