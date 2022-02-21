package hoot.system.Exception;

public class CouldNotDeleteException extends Exception
{
    public CouldNotDeleteException(String entityName)
    {
        super("Entity " + entityName + " could not be deleted.");
    }
}
