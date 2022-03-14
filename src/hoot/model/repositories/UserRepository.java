package hoot.model.repositories;

import hoot.model.cache.UserCacheInterface;
import hoot.model.entities.User;
import hoot.model.search.DefaultSearchCriteria;
import hoot.model.search.SearchCriteriaInterface;
import hoot.system.Database.QueryBuilder;
import hoot.system.Database.QueryResult;
import hoot.system.Database.QueryResultRow;
import hoot.system.Exception.CouldNotDeleteException;
import hoot.system.Exception.CouldNotSaveException;
import hoot.system.Exception.EntityNotFoundException;
import hoot.system.ObjectManager.ObjectManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

public class UserRepository extends AbstractRepository<User>
{
    private final UserCacheInterface userCache;

    private final FollowerRepository followerRepository;

    public UserRepository()
    {
        super();

        this.followerRepository = (FollowerRepository) ObjectManager.get(FollowerRepository.class);
        this.userCache          = (UserCacheInterface) ObjectManager.get(UserCacheInterface.class);
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
            QueryBuilder queryBuilder = (QueryBuilder) ObjectManager.create(QueryBuilder.class);
            queryBuilder.SELECT.add("*");
            queryBuilder.FROM = "User u";
            queryBuilder.addWhere("id = ?", Integer.toString(id));

            PreparedStatement statement = queryBuilder.build(connection);
            statement.setInt(1, id);

            QueryResultRow resultRow = this.statementFetcher.fetchOne(statement);
            connection.close();

            user = this.mapResultSetToUser(resultRow);
        } catch (SQLException e) {
            throw new EntityNotFoundException("User with ID: " + id);
        }

        this.userCache.put(user);

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
            QueryBuilder queryBuilder = (QueryBuilder) ObjectManager.create(QueryBuilder.class);
            queryBuilder.SELECT.add("*");
            queryBuilder.FROM = "User";
            queryBuilder.addWhere("username = ?", username);

            PreparedStatement statement = queryBuilder.build(connection);

            QueryResultRow resultRow = this.statementFetcher.fetchOne(statement);
            connection.close();

            user = this.mapResultSetToUser(resultRow);
        } catch (SQLException e) {
            throw new EntityNotFoundException("User with username: " + username);
        }

        this.userCache.put(user);

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
            queryBuilder.SELECT.add("id");
            queryBuilder.FROM = "User";

            PreparedStatement statement   = queryBuilder.build(connection);
            QueryResult       queryResult = this.statementFetcher.fetchAll(statement);
            connection.close();

            for (QueryResultRow resultRow : queryResult) {
                User user = this.getById((int) resultRow.get("User.id"));
                users.add(user);
            }
        } catch (SQLException e) {
            this.log("UserRepository.getList(): " + e.getMessage());
        }

        return users;
    }

    public Long getUserQuantity() throws SQLException
    {
        DefaultSearchCriteria criteria = (DefaultSearchCriteria) ObjectManager.create(DefaultSearchCriteria.class);

        return this.getUserQuantityBySearchCriteria(criteria);
    }

    public Long getUserQuantityBySearchCriteria(SearchCriteriaInterface searchCriteriaInterface) throws SQLException
    {
        try (Connection connection = this.getConnection()) {
            QueryBuilder queryBuilder = searchCriteriaInterface.getQueryBuilder();
            queryBuilder.SELECT.add("count(*) AS quantity");
            queryBuilder.FROM = "User";

            PreparedStatement statement = queryBuilder.build(connection);
            QueryResultRow    resultRow = this.statementFetcher.fetchOne(statement);
            connection.close();

            return (Long) resultRow.get("quantity");
        } catch (SQLException e) {
            this.log(e.getMessage());
            throw e;
        }
    }

    private void create(User user) throws CouldNotSaveException
    {
        try (Connection connection = this.getConnection()) {
            String sqlStatement = "INSERT INTO User (username, imagePath, passwordHash) VALUES (?, ?, ?)";

            PreparedStatement pss = connection.prepareStatement(sqlStatement);
            pss.setString(1, user.username);
            pss.setString(2, user.imagePath);
            pss.setString(3, user.passwordHash);

            int rowCount = this.statementFetcher.executeUpdate(pss);

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
            String sqlStatement = "UPDATE User " + "SET username = ?, imagePath = ?, passwordHash = ?, lastLogin = ? "
                                  + "WHERE id = ?";

            PreparedStatement pss = connection.prepareStatement(sqlStatement);

            pss.setString(1, user.username);
            pss.setString(2, user.imagePath);
            pss.setString(3, user.passwordHash);
            pss.setTimestamp(4, this.getSQLTimestampFromLocalDateTime(user.lastLogin));
            pss.setInt(5, user.id);

            int rowCount = this.statementFetcher.executeUpdate(pss);

            if (rowCount == 0) {
                throw new SQLException("User with username " + user.username + " was not saved.");
            }
        } catch (SQLException e) {
            throw new CouldNotSaveException("User with username " + user.username);
        } finally {
            this.userCache.purge(user);
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
        try (Connection connection = this.getConnection()) {
            PreparedStatement pss = connection.prepareStatement("DELETE FROM User WHERE id = ?");
            pss.setInt(1, user.id);

            int rowCount = this.statementFetcher.executeUpdate(pss);

            if (rowCount == 0) {
                throw new CouldNotDeleteException("User with username " + user.username);
            }
        } catch (SQLException e) {
            throw new CouldNotDeleteException("User with username " + user.username);
        } finally {
            this.userCache.purge(user);
        }
    }

    private User mapResultSetToUser(QueryResultRow resultRow) throws SQLException
    {
        User user = (User) ObjectManager.create(User.class);
        user.id = (int) resultRow.get("User.id");

        User searchedUser = this.userCache.get(user.id);
        if (searchedUser != null) {
            user = searchedUser;
        }

        user.username     = (String) resultRow.get("User.username");
        user.imagePath    = (String) resultRow.get("User.imagePath");
        user.passwordHash = (String) resultRow.get("User.passwordHash");
        user.created      = this.getLocalDateTimeFromSQLTimestamp((Timestamp) resultRow.get("User.created"));
        user.lastLogin    = this.getLocalDateTimeFromSQLTimestamp((Timestamp) resultRow.get("User.lastLogin"));

        try {
            user.followerCount = this.followerRepository.getFollowerCountForUser(user.id);
            user.followsCount  = this.followerRepository.getFollowsCountForUser(user.id);
        } catch (EntityNotFoundException ignore) {
        }

        return user;
    }
}
