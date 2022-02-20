package hoot.model.repositories;

import hoot.system.Exception.CouldNotDeleteException;
import hoot.system.Exception.CouldNotSaveException;
import hoot.system.Exception.EntityNotFoundException;

import java.io.PrintWriter;

public interface RepositoryInterface<Type>
{
    public Type getById(int id) throws EntityNotFoundException;

    public void save(Type user) throws CouldNotSaveException;

    public void delete(Type user) throws CouldNotDeleteException;
}
