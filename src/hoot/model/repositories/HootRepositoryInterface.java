package hoot.model.repositories;

import hoot.front.api.dto.hoot.HootDTO;
import hoot.system.Exception.CouldNotDeleteException;
import hoot.system.Exception.CouldNotSaveException;
import hoot.system.Exception.EntityNotFoundException;

public interface HootRepositoryInterface
{
    public HootDTO getById(int id) throws EntityNotFoundException;

    public void save(HootDTO user) throws CouldNotSaveException;

    public void delete(HootDTO user) throws CouldNotDeleteException;
}
