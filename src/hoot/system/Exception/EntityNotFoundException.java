package hoot.system.Exception;

public class EntityNotFoundException extends Exception
{
    EntityNotFoundException(String entityName)
    {
        super("Entity " + entityName + " not found.");
    }
}
