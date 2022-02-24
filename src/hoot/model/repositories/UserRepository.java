package hoot.model.repositories;

import hoot.model.entities.User;
import hoot.model.search.SearchCriteriaInterface;
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

            String            sqlStatement = "select id, username, imagePath, passwordHash, lastLogin, created from User where id = ?";
            PreparedStatement pss          = connection.prepareStatement(sqlStatement);
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
            throw new EntityNotFoundException("User with ID: " + id);
        }
    }

    @Override
    public ArrayList<User> getList(SearchCriteriaInterface searchCriteria)
    {
        ArrayList<User> users = new ArrayList<>();

        try {
            Connection connection       = this.getConnection();
            PreparedStatement statement = searchCriteria.getQueryStatement(connection);
            ResultSet resultSet         = statement.executeQuery();

            while (resultSet.next()) {
                LocalDateTime lastLogin = resultSet
                        .getTimestamp("lastLogin")
                        .toInstant()
                        .atZone(ZoneId.of("Europe/Berlin"))
                        .toLocalDateTime();

                LocalDateTime created = resultSet
                        .getTimestamp("created")
                        .toInstant()
                        .atZone(ZoneId.of("Europe/Berlin"))
                        .toLocalDateTime();

                User user = new User();
                user.username     = resultSet.getString("username");
                user.imagePath    = resultSet.getString("imagePath");
                user.passwordHash = resultSet.getString("passwordHash");

                users.add(user);
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            // TODO: Maybe logging and stuff, maybe not, this could
        }

        return users;
    }

    /**
     * Create a new User and save it directly to the DB.
     * @param username the new User's name. Must be unique.
     * @param imagePath Path to the profile picture.
     * @param passwordHash SHA2 hashed Password.
     * @return An Object representing the just inserted User. You may change the object and save it with save(User).
     * @throws CouldNotSaveException if any errors (SQL or otherwise) occurred.
     */
    public User create(String username, String imagePath, String passwordHash) throws CouldNotSaveException
    {
        try {
            Connection        connection   = this.getConnection();
            String            sqlStatement = "insert into User (username, imagePath, passwordHash) values (?, ?, ?)";
            PreparedStatement pss          = connection.prepareStatement(sqlStatement, PreparedStatement.RETURN_GENERATED_KEYS);

            pss.setString(1, username);
            pss.setString(2, imagePath);
            pss.setString(3, passwordHash);
            int rowCount = pss.executeUpdate();

            ResultSet rs = pss.getGeneratedKeys();

            if (rowCount == 0) {
                throw new CouldNotSaveException("new User with username " + username);
            }

            rs.next();

            int userid = rs.getInt(1);

            rs.close();
            pss.close();
            connection.close();

            return this.getById(userid);
        } catch (SQLException e) {
            this.log(e.getMessage());
            throw new CouldNotSaveException("new User with username " + username);
        } catch (EntityNotFoundException e) {
            this.log(e.getMessage());
            this.log("Just inserted a User but could not find it afterwards! This should never happen.");
            throw new CouldNotSaveException("new User with username " + username + " (disappeared after insert)");
        }
    }

    /**
     * Save changes to an already existing User in the DB
     * @param user a User object that was previously returned from the getById() method. DO NOT CREATE ONE ON YOUR OWN.
     * @throws CouldNotSaveException if any SQL errors occurred.
     */
    @Override
    public void save(User user) throws CouldNotSaveException
    {
        if (user.id == null) {
            this.create(user.username, user.imagePath, user.passwordHash);
            return;
        }

        try {
            Connection        connection   = this.getConnection();
            String            sqlStatement = "update User set username = ?, imagePath = ?, passwordHash = ? where id = ?";
            PreparedStatement pss          = connection.prepareStatement(sqlStatement);
            pss.setString(1, user.username);
            pss.setString(2, user.imagePath);
            pss.setString(3, user.passwordHash);
            pss.setInt(4, user.id);
            int rowCount = pss.executeUpdate();

            if (rowCount == 0) {
                throw new CouldNotSaveException("User with username " + user.username);
            }

            pss.close();
            connection.close();
        } catch (SQLException e) {
            this.log(e.getMessage());
        }
    }

    /**
     * Delete an existing User from the DB.
     * @param user A User object that was previously returned from the getById() method. DO NOT CREATE ONE ON YOUR OWN.
     * @throws CouldNotDeleteException if any SQL errors occurred.
     */
    @Override
    public void delete(User user) throws CouldNotDeleteException
    {
        try {
            Connection connection = this.getConnection();

            String            sqlStatement = "delete from User where id = ?";
            PreparedStatement pss          = connection.prepareStatement(sqlStatement);
            pss.setInt(1, user.id);
            int rowCount = pss.executeUpdate();

            if (rowCount == 0) {
                throw new CouldNotDeleteException("User with username " + user.username);
            }

            pss.close();
            connection.close();
        } catch (SQLException e) {
            this.log(e.getMessage());
            throw new CouldNotDeleteException("User with username " + user.username);
        }
    }
}
