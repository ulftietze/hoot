package hoot.system.Exception;

import java.sql.SQLException;

public class CouldNotDeleteException extends SQLException
{
    public CouldNotDeleteException(String entityName)
    {
        super("Entity " + entityName + " could not be deleted.");
    }
}
