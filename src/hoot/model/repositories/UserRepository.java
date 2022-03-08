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

public class UserRepository extends AbstractRepository<User>
{
    private final UserCache userCache;

    private final FollowerRepository followerRepository;

    public UserRepository()
    {
        super();

        this.followerRepository = (FollowerRepository) ObjectManager.get(FollowerRepository.class);
        this.userCache          = (UserCache) ObjectManager.get(UserCache.class);
    }

    public Integer getAllUsersCount()
    {
        try (Connection connection = this.getConnection()) {
            QueryBuilder queryBuilder = (QueryBuilder) ObjectManager.create(QueryBuilder.class);
            queryBuilder.SELECT.add("count(*) AS quantity");
            queryBuilder.FROM = "User";

            PreparedStatement statement = queryBuilder.build(connection);
            ResultSet resultSet         = statement.executeQuery();

            resultSet.next();

            int quantity = resultSet.getInt("quantity");

            resultSet.close();
            statement.close();
            connection.close();

            return quantity;
        } catch (SQLException e) {
            this.log(e.getMessage());
        }

        return 0;
    }

    /**
     * Try to return a User object representing the database entry with the given id.
     * TODO: Check if synchronisation is required!
     *
     * @param id User.id (Primary Key)
     * @return User object if the user was found and no SQL errors occurred, null otherwise
     */
    public User getById(int id) throws EntityNotFoundException
    {
        User user = this.userCache.get(id);

        if (user != null) {
            return user;
        }

        try (Connection connection = this.getConnection()) {
            QueryBuilder qb = (QueryBuilder) ObjectManager.create(QueryBuilder.class);
            qb.SELECT.add("*");
            qb.FROM = "User u";
            qb.WHERE.add("id = ?");
            qb.PARAMETERS.add(Integer.toString(id));

            PreparedStatement pss = qb.build(connection);
            pss.setInt(1, id);
            ResultSet rs = pss.executeQuery();

            rs.next(); // will throw SQLException if result set is empty

            user = this.mapResultSetToUser(rs);

            rs.close();
            pss.close();
        } catch (SQLException e) {
            throw new EntityNotFoundException("User with ID: " + id);
        }

        return user;
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
        User user = this.userCache.get(username);

        if (user != null) {
            return user;
        }

        try (Connection connection = this.getConnection()) {
            QueryBuilder qb = (QueryBuilder) ObjectManager.create(QueryBuilder.class);
            qb.SELECT.add("*");
            qb.FROM = "User";
            qb.addWhere("username = ?", username);
            PreparedStatement pss = qb.build(connection);

            ResultSet rs = pss.executeQuery();
            rs.next(); // will throw SQLException if result set is empty

            user = this.mapResultSetToUser(rs);

            rs.close();
            pss.close();
        } catch (SQLException e) {
            throw new EntityNotFoundException("User with username: " + username);
        }

        return user;
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

        try (Connection connection = this.getConnection()) {
            QueryBuilder queryBuilder = searchCriteria.getQueryBuilder();
            queryBuilder.SELECT.add("*");
            queryBuilder.FROM = "User";

            PreparedStatement statement  = queryBuilder.build(connection);
            ResultSet         resultSet  = statement.executeQuery();

            while (resultSet.next()) {
                User user = this.mapResultSetToUser(resultSet);
                users.add(user);
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            this.log("UserRepository.getList(): " + e.getMessage());
        }

        return users;
    }

    private void create(User user) throws CouldNotSaveException
    {
        try (Connection connection = this.getConnection()) {
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
        } catch (SQLException e) {
            throw new CouldNotSaveException("new User with username " + user.username);
        }
    }

    /**
     * Save a User in the DB.<br>
     * If User.id is set, this method will perform an update to an existing user. Otherwise, a new
     * User will be inserted.
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

        try (Connection connection = this.getConnection()) {
            String sqlStatement = "UPDATE User "
                                  + "SET username = ?, imagePath = ?, passwordHash = ?, lastLogin = ? "
                                  + "WHERE id = ?";

            PreparedStatement pss = connection.prepareStatement(sqlStatement);

            pss.setString(1, user.username);
            pss.setString(2, user.imagePath);
            pss.setString(3, user.passwordHash);
            pss.setTimestamp(4, this.getSQLTimestampFromLocalDateTime(user.lastLogin));
            pss.setInt(5, user.id);

            int rowCount = pss.executeUpdate();
            pss.close();

            if (rowCount == 0) {
                throw new SQLException("User with username " + user.username + " was not saved.");
            }
        } catch (SQLException e) {
            throw new CouldNotSaveException("User with username " + user.username);
        }

        this.userCache.purge(user);
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
        try (Connection connection = this.getConnection()) {
            PreparedStatement pss = connection.prepareStatement("DELETE FROM User WHERE id = ?");
            pss.setInt(1, user.id);

            int rowCount = pss.executeUpdate();
            pss.close();

            if (rowCount == 0) {
                throw new CouldNotDeleteException("User with username " + user.username);
            }
        } catch (SQLException e) {
            throw new CouldNotDeleteException("User with username " + user.username);
        }

        this.userCache.purge(user);
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException
    {
        User user = (User) ObjectManager.create(User.class);
        user.id = rs.getInt("id");

        User searchedUser = this.userCache.get(user.id);
        if (searchedUser != null) {
            user = searchedUser;
        }

        user.username     = rs.getString("username");
        user.imagePath    = rs.getString("imagePath");
        user.passwordHash = rs.getString("passwordHash");
        user.created      = this.getLocalDateTimeFromSQLTimestamp(rs.getTimestamp("created"));
        user.lastLogin    = this.getLocalDateTimeFromSQLTimestamp(rs.getTimestamp("lastLogin"));

        try {
            user.followerCount = this.followerRepository.getFollowerCountForUser(user.id);
        } catch (EntityNotFoundException ignore) {
        }

        if (searchedUser != null) {
            this.userCache.put(user);
        }

        return user;
    }
}
