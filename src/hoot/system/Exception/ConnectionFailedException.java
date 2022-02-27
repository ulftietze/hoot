package hoot.system.Exception;

import java.sql.SQLException;

public class ConnectionFailedException extends SQLException
{
    public ConnectionFailedException()
    {
        super("No SQL Connection could be established.");
    }
}
