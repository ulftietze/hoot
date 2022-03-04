package hoot.model.repositories;

import hoot.model.cache.UserCache;
import hoot.model.entities.User;
import hoot.model.search.SearchCriteriaInterface;
import hoot.system.Database.QueryBuilder;
import hoot.system.Exception.CouldNotDeleteException;
import hoot.system.Exception.CouldNotSaveException;
import hoot.system.Exception.EntityNotFoundException;
import hoot.system.ObjectManager.ObjectManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

public class UserRepository extends AbstractRepository<User>
{
    /**
     * Try to return a User object representing the database entry with the given id.
     * TODO: Check if synchronisation is required!
     *
     * @param id User.id (Primary Key)
     * @return User object if the user was found and no SQL errors occurred, null otherwise
     */
    public User getById(int id) throws EntityNotFoundException
    {
        UserCache userCache  = (UserCache) ObjectManager.get(UserCache.class);
        User      cachedUser = userCache.get(id);
        if (cachedUser != null) {
            return cachedUser;
        }

        try {
            // TODO: Build with query builder
            Connection connection = this.getConnection();
            String
                    sqlStatement
                    = "select id, username, imagePath, passwordHash, lastLogin, created from User where id = ?";

            QueryBuilder qb = (QueryBuilder) ObjectManager.create(QueryBuilder.class);
            qb.SELECT.add("*");
            qb.FROM = "User u";
            qb.WHERE.add("id = ?");
            qb.PARAMETERS.add(Integer.toString(id));

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

            User user = this.mapResultSetToUser(rs);

            rs.close();
            pss.close();
            connection.close();

            return user;
        } catch (SQLException e) {
            this.log(e.getMessage());
            throw new EntityNotFoundException("User with ID: " + id);
        }
    }

    /**
     * Try to return a User object representing the database entry by given username.
     * TODO: Check if synchronisation is required!
     *
     * @param username User.id (Primary Key)
     * @return User object if the user was found and no SQL errors occurred, null otherwise
     */
    public User getByUsername(String username) throws EntityNotFoundException
    {
        UserCache userCache  = (UserCache) ObjectManager.get(UserCache.class);
        User      cachedUser = userCache.get(username);
        if (cachedUser != null) {
            return cachedUser;
        }

        try {
            Connection connection = this.getConnection();

            // TODO: Build with query builder
            String            sqlStatement = "select * from User where username = ?";
            PreparedStatement pss          = connection.prepareStatement(sqlStatement);
            pss.setString(1, username);
            ResultSet rs = pss.executeQuery();

            // TODO: this code is similar to getById() and getList(), lets check if we can prevent duplicated code
            rs.next();          // will throw SQLException if result set is empty
            if (!rs.isLast()) { // throw Exception if result set contains more than one result
                throw new EntityNotFoundException("User with username " + username);
            }

            // TODO: Do we need this check?
            if (!Objects.equals(username, rs.getString("username"))) {
                throw new EntityNotFoundException(
                        "User with username " + username + " (id " + rs.getInt("id") + " was returned)");
            }

            User user = this.mapResultSetToUser(rs);

            rs.close();
            pss.close();
            connection.close();

            return user;
        } catch (SQLException e) {
            this.log(e.getMessage());
            throw new EntityNotFoundException("User with username: " + username);
        }
    }

    /**
     * TODO: Check if synchronisation is required!
     *
     * @param searchCriteria
     * @return
     */
    @Override
    public ArrayList<User> getList(SearchCriteriaInterface searchCriteria)
    {
        ArrayList<User> users = new ArrayList<>();

        try {
            QueryBuilder queryBuilder = searchCriteria.getQueryBuilder();
            queryBuilder.SELECT.add("*");
            queryBuilder.FROM = "User";

            Connection        connection = this.getConnection();
            PreparedStatement statement  = queryBuilder.build(connection);
            ResultSet         resultSet  = statement.executeQuery();

            while (resultSet.next()) {
                User user = this.mapResultSetToUser(resultSet);
                users.add(user);
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            this.log("UserRepository.getList(): " + e.getMessage());
        }

        return users;
    }

    private void create(User user) throws CouldNotSaveException
    {
        try {
            Connection        connection   = this.getConnection();
            String            sqlStatement = "insert into User (username, imagePath, passwordHash) values (?, ?, ?)";
            PreparedStatement pss          = connection.prepareStatement(sqlStatement);

            pss.setString(1, user.username);
            pss.setString(2, user.imagePath);
            pss.setString(3, user.passwordHash);
            int rowCount = pss.executeUpdate();

            pss.close();
            connection.close();

            if (rowCount == 0) {
                throw new CouldNotSaveException("new User with username " + user.username);
            }
        } catch (EntityNotFoundException e) {
            this.log(e.getMessage());
            this.log("Just inserted a User but could not find it afterwards! This should never happen.");
            throw new CouldNotSaveException("new User with username " + user.username + " (disappeared after insert)");
        } catch (SQLException e) {
            this.log(e.getMessage());
            throw new CouldNotSaveException("new User with username " + user.username);
        }
    }

    /**
     * Save a User in the DB.<br>
     * If User.id is set, this method will perform an update to an existing user. Otherwise a new User will be inserted.
     *
     * @param user a User entity
     * @throws CouldNotSaveException if any SQL errors occurred.
     */
    @Override
    public void save(User user) throws CouldNotSaveException
    {
        if (user.id == null) {
            this.create(user);
            return;
        }

        try {
            String
                    sqlStatement
                    = "update User set username = ?, imagePath = ?, passwordHash = ?, lastLogin = ? where id = ?";

            Connection        connection = this.getConnection();
            PreparedStatement pss        = connection.prepareStatement(sqlStatement);

            pss.setString(1, user.username);
            pss.setString(2, user.imagePath);
            pss.setString(3, user.passwordHash);
            pss.setTimestamp(4, this.getSQLTimestampFromLocalDateTime(user.lastLogin));
            pss.setInt(5, user.id);
            int rowCount = pss.executeUpdate();

            pss.close();
            connection.close();

            if (rowCount == 0) {
                throw new CouldNotSaveException("User with username " + user.username);
            }
        } catch (SQLException e) {
            this.log(e.getMessage());
        }
    }

    /**
     * Delete an existing User from the DB.
     *
     * @param user a User entity
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

            pss.close();
            connection.close();

            if (rowCount == 0) {
                throw new CouldNotDeleteException("User with username " + user.username);
            }
        } catch (SQLException e) {
            this.log(e.getMessage());
            throw new CouldNotDeleteException("User with username " + user.username);
        }
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException
    {
        UserCache userCache = (UserCache) ObjectManager.get(UserCache.class);
        User      user      = (User) ObjectManager.create(User.class);

        user.id = rs.getInt("id");

        User searchedUser = userCache.get(user.id);
        if (searchedUser != null) {
            user = searchedUser;
        }

        user.username     = rs.getString("username");
        user.imagePath    = rs.getString("imagePath");
        user.passwordHash = rs.getString("passwordHash");
        user.created      = this.getLocalDateTimeFromSQLTimestamp(rs.getTimestamp("created"));
        user.lastLogin    = this.getLocalDateTimeFromSQLTimestamp(rs.getTimestamp("lastLogin"));

        try {
            FollowerRepository fr = (FollowerRepository) ObjectManager.get(FollowerRepository.class);
            user.followerCount = fr.getFollowerCountForUser(user.id);
        } catch (EntityNotFoundException ignore) {
        }

        if (searchedUser != null) {
            userCache.put(user);
        }

        return user;
    }
}
