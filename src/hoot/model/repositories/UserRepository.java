package hoot.model.repositories;

import hoot.front.api.dto.user.UserDTO;
import hoot.model.search.SearchCriteriaInterface;
import hoot.model.search.UserSearchCriteria;
import hoot.system.Exception.ConnectionFailedException;
import hoot.system.Exception.CouldNotDeleteException;
import hoot.system.Exception.CouldNotSaveException;
import hoot.system.Exception.EntityNotFoundException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class UserRepository extends AbstractRepository<UserDTO>
{
    @Override
    public UserDTO getById(int id) throws EntityNotFoundException, ConnectionFailedException
    {
        try {
            Connection connection = this.getConnection();
            PreparedStatement pss = connection.prepareStatement("select id, username, imagePath from User where id = ?");
            pss.setInt(1, id);
            ResultSet rs = pss.executeQuery();

            rs.next(); // will throw SQLException if result set is empty
            if (!rs.isLast()) {
                throw new EntityNotFoundException("User");
            }

            UserDTO user = new UserDTO();

            user.id       = rs.getInt("id");
            user.username = rs.getString("username");
            user.imageUrl = rs.getString("imagePath");

            rs.close();
            pss.close();

            connection.close();

            return user;
        } catch (SQLException e) {
            this.getLogger().log("Das hat nicht geklappt");
            throw new EntityNotFoundException("User");
        }
    }

    @Override
    public ArrayList<UserDTO> getList(SearchCriteriaInterface searchCriteria)
    {
        UserSearchCriteria us = (UserSearchCriteria) searchCriteria;
        return null;
    }

    @Override
    public void save(UserDTO user) throws CouldNotSaveException
    {

    }

    @Override
    public void delete(UserDTO user) throws CouldNotDeleteException
    {

    }
}
