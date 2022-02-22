package hoot.model.repositories;

import hoot.model.search.SearchCriteriaInterface;
import hoot.system.Exception.CouldNotDeleteException;
import hoot.system.Exception.CouldNotSaveException;
import hoot.system.Exception.EntityNotFoundException;
import hoot.system.Exception.ConnectionFailedException;

import java.util.ArrayList;

public abstract class AbstractRepository<Type>
{
    public abstract Type getById(int id) throws EntityNotFoundException, ConnectionFailedException;

    public abstract ArrayList<Type> getList(SearchCriteriaInterface searchCriteria);

    public abstract void save(Type type) throws CouldNotSaveException;

    public abstract void delete(Type type) throws CouldNotDeleteException;
}
