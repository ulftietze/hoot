package hoot.system.Exception;

public class CouldNotSaveException extends Exception
{
    public CouldNotSaveException(String entityName)
    {
        super("Entity " + entityName + " could not be saved.");
    }
}