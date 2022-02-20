package hoot.system.Exception;

public class CouldNotDeleteException extends Exception
{
    CouldNotDeleteException(String entityName)
    {
        super("Entity " + entityName + " could not be deleted.");
    }
}
