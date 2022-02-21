package hoot.system.Exception;

public class ConnectionFailedException extends Exception
{
    public ConnectionFailedException()
    {
        super("No SQL Connection could be established.");
    }
}
