package hoot.system.Exception;

public class EntityNotFoundException extends Exception
{
    public EntityNotFoundException(String entityName)
    {
        super("Entity " + entityName + " not found.");
    }
}
