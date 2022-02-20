package hoot.model.repositories;

import hoot.front.api.dto.user.UserDTO;
import hoot.system.Exception.CouldNotDeleteException;
import hoot.system.Exception.CouldNotSaveException;
import hoot.system.Exception.EntityNotFoundException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class UserRepository implements UserRepositoryInterface
{
    public UserDTO getById(int id) throws EntityNotFoundException
    {
        Connection connection;

        try {
            connection = DriverManager.getConnection("...");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        // connection.query("Select * from user;");
        // result = connection.fetchAll();

        // userDTO = new UserDTO();
        // mapper.map(result, userDTO);
        // return userDTO;

        return null;
    }

    public void save(UserDTO user) throws CouldNotSaveException
    {

    }

    public void delete(UserDTO user) throws CouldNotDeleteException
    {

    }
}
