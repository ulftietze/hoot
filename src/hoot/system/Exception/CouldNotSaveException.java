package hoot.system.Exception;

import java.sql.SQLException;

public class CouldNotSaveException extends SQLException
{
    public CouldNotSaveException(String entityName)
    {
        super("Entity " + entityName + " could not be saved.");
    }
}
