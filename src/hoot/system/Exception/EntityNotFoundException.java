package hoot.system.Exception;

import java.sql.SQLException;

public class EntityNotFoundException extends SQLException
{
    public EntityNotFoundException(String entityName)
    {
        super("Entity " + entityName + " not found.");
    }
}
