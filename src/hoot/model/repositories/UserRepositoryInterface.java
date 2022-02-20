package hoot.model.repositories;

import hoot.front.api.dto.user.UserDTO;
import hoot.system.Exception.CouldNotDeleteException;
import hoot.system.Exception.CouldNotSaveException;
import hoot.system.Exception.EntityNotFoundException;

public interface UserRepositoryInterface
{
    public UserDTO getById(int id) throws EntityNotFoundException;

    public void save(UserDTO user) throws CouldNotSaveException;

    public void delete(UserDTO user) throws CouldNotDeleteException;
}
