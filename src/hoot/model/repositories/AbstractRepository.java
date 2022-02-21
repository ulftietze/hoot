package hoot.model.repositories;

import hoot.model.search.SearchCriteriaInterface;
import hoot.system.Database.DatabaseConnectionPool;
import hoot.system.Exception.CouldNotDeleteException;
import hoot.system.Exception.CouldNotSaveException;
import hoot.system.Exception.EntityNotFoundException;
import hoot.system.Exception.ConnectionFailedException;

import java.sql.Connection;
import java.util.ArrayList;

public abstract class AbstractRepository<Type>
{
    private final DatabaseConnectionPool pool;

    public AbstractRepository()
    {
        this.pool = DatabaseConnectionPool.getPool();
    }

    protected Connection getConnection() throws ConnectionFailedException
    {
        if (this.pool == null) {
            throw new ConnectionFailedException();
        }
        return pool.get();
    }

    protected void returnConnection(Connection connection) throws ConnectionFailedException
    {
        if (this.pool == null) {
            throw new ConnectionFailedException();
        }
        pool.put(connection);
    }

    public abstract Type getById(int id) throws EntityNotFoundException, ConnectionFailedException;

    public abstract ArrayList<Type> getList(SearchCriteriaInterface searchCriteria);

    public abstract void save(Type type) throws CouldNotSaveException;

    public abstract void delete(Type type) throws CouldNotDeleteException;
}
