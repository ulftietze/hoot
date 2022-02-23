package hoot.model.repositories;

import hoot.model.search.SearchCriteriaInterface;
import hoot.system.Exception.ConnectionFailedException;
import hoot.system.Exception.CouldNotDeleteException;
import hoot.system.Exception.CouldNotSaveException;
import hoot.system.Exception.EntityNotFoundException;
import hoot.system.Logger.LoggerInterface;
import hoot.system.ObjectManager.ObjectManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public abstract class AbstractRepository<Type>
{
    protected synchronized Connection getConnection() throws SQLException
    {
        DataSource ds = (DataSource) ObjectManager.get(DataSource.class);
        return ds.getConnection();
    }

    protected synchronized void log(String message)
    {
        LoggerInterface logger = (LoggerInterface) ObjectManager.get(LoggerInterface.class);
        logger.log(message);
    }

    public abstract Type getById(int id) throws EntityNotFoundException, ConnectionFailedException;

    public abstract ArrayList<Type> getList(SearchCriteriaInterface searchCriteria);

    public abstract Type create() throws CouldNotSaveException;

    public abstract void save(Type type) throws CouldNotSaveException;

    public abstract void delete(Type type) throws CouldNotDeleteException;
}
