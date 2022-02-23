package hoot.model.repositories;

import hoot.model.entities.User;
import hoot.model.search.SearchCriteriaInterface;
import hoot.model.search.UserSearchCriteria;
import hoot.system.Exception.CouldNotDeleteException;
import hoot.system.Exception.CouldNotSaveException;
import hoot.system.Exception.EntityNotFoundException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

public class UserRepository extends AbstractRepository<User>
{
    /**
     * Try to return a User object representing the database entry with the given id.
     *
     * @param id User.id (Primary Key)
     * @return User object if the user was found and no SQL errors occurred, null otherwise
     */
    // TODO: Check if synchronisation is required!
    @Override
    public User getById(int id) throws EntityNotFoundException
    {
        try {
            Connection connection = this.getConnection();

            String sqlStatement = "select id, username, imagePath, passwordHash, lastLogin, created from User where id = ?";
            PreparedStatement pss = connection.prepareStatement(sqlStatement);
            pss.setInt(1, id);
            ResultSet rs = pss.executeQuery();

            rs.next();          // will throw SQLException if result set is empty
            if (!rs.isLast()) { // throw Exception if result set contains more than one result
                throw new EntityNotFoundException("User with id " + id);
            }

            if (id != rs.getInt("id")) {
                throw new EntityNotFoundException("User with id " + id + " (id " + rs.getInt("id") + " was returned)");
            }

            LocalDateTime lastLogin = rs
                    .getTimestamp("lastLogin")
                    .toInstant()
                    .atZone(ZoneId.of("Europe/Berlin"))
                    .toLocalDateTime();

            LocalDateTime created = rs
                    .getTimestamp("created")
                    .toInstant()
                    .atZone(ZoneId.of("Europe/Berlin"))
                    .toLocalDateTime();

            User user = new User(id, lastLogin, created);

            user.username     = rs.getString("username");
            user.imagePath    = rs.getString("imagePath");
            user.passwordHash = rs.getString("passwordHash");

            rs.close();
            pss.close();
            connection.close();

            return user;
        } catch (EntityNotFoundException | SQLException e) {
            this.log(e.getMessage());
            throw new EntityNotFoundException("User");
        }
    }

    @Override
    public ArrayList<User> getList(SearchCriteriaInterface searchCriteria)
    {
        UserSearchCriteria us = (UserSearchCriteria) searchCriteria;
        return null;
    }

    @Override
    public User create() throws CouldNotSaveException
    {
        // Generate random but unique userName and save it
        // insert generated username in table and check for SQL Errors (username already exists?)
        // search table for username (only one result, username is unique!)
        // call getUserById with the id that was returned by the DB
        // set username, password, etc.
        // return user;
        return null;
    }

    @Override
    public void save(User user) throws CouldNotSaveException
    {

    }

    @Override
    public void delete(User user) throws CouldNotDeleteException
    {

    }
}
