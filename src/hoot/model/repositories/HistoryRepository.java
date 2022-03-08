package hoot.model.repositories;

import hoot.model.entities.History;
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

public class HistoryRepository extends AbstractRepository<History>
{
    public History getById(Long id) throws EntityNotFoundException
    {
        History history;

        try (Connection connection = this.getConnection()) {
            QueryBuilder queryBuilder = (QueryBuilder) ObjectManager.create(QueryBuilder.class);
            queryBuilder.SELECT.add("*");
            queryBuilder.FROM = "History";
            queryBuilder.WHERE.add("id = ?");
            queryBuilder.PARAMETERS.add(id);

            PreparedStatement statement = queryBuilder.build(connection);

            ResultSet resultSet = statement.executeQuery();
            resultSet.next();

            history = this.mapResultSetToHistory(resultSet);

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            //this.log("HistoryRepository.getById(): " + e.getSQLState() + " " + e.getMessage());
            throw new EntityNotFoundException("History");
        }

        return history;
    }

    @Override
    public ArrayList<History> getList(SearchCriteriaInterface searchCriteria) throws EntityNotFoundException
    {
        ArrayList<History> allHistorys = new ArrayList<>();

        try (Connection connection = this.getConnection()) {
            QueryBuilder queryBuilder = searchCriteria.getQueryBuilder();
            queryBuilder.FROM = "History";

            PreparedStatement statement = queryBuilder.build(connection);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                allHistorys.add(this.mapResultSetToHistory(resultSet));
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            //this.log("HistoryRepository.getList(): " + e.getMessage());
            throw new EntityNotFoundException("History");
        }

        return allHistorys;
    }

    public void create(History history) throws CouldNotSaveException
    {
        try (Connection connection = this.getConnection()) {
            String sqlStatement =
                    "INSERT INTO History (currentLoggedIn, loginsPerSixHours, registrationsPerSixHours, "
                    + "postsPerMinute, requestsPerSecond, requestsLoggedInPerSecond, currentlyRegisteredUsers, "
                    + "trendingHashtags, systemLoadAverage, systemCPULoad, processCPULoad, "
                    + "memoryMax, memoryTotal, memoryFree, memoryUsed, threadCount, threadCountTotal) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) "
                    + "RETURNING id, timestamp";

            PreparedStatement statement = connection.prepareStatement(sqlStatement);

            statement.setInt(1, history.currentLoggedIn);
            statement.setFloat(2, history.loginsPerSixHours);
            statement.setFloat(3, history.registrationsPerSixHours);
            statement.setFloat(4, history.postsPerMinute);
            statement.setFloat(5, history.requestsPerSecond);
            statement.setFloat(6, history.requestsLoggedInPerSecond);
            statement.setInt(7, history.currentlyRegisteredUsers);
            statement.setString(8, history.getCommaSeperatedTags());
            statement.setDouble(9, history.systemLoadAverage);
            statement.setDouble(10, history.systemCPULoad);
            statement.setDouble(11, history.processCPULoad);
            statement.setInt(12, history.memoryMax);
            statement.setInt(13, history.memoryTotal);
            statement.setInt(14, history.memoryFree);
            statement.setInt(15, history.memoryUsed);
            statement.setInt(16, history.threadCount);
            statement.setInt(17, history.threadCountTotal);

            ResultSet resultSet = statement.executeQuery();

            resultSet.next();

            history.id        = resultSet.getLong(1);
            history.timestamp = this.getLocalDateTimeFromSQLTimestamp(resultSet.getTimestamp(2));

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            throw new CouldNotSaveException("History");
        }
    }

    @Override
    public void save(History history) throws CouldNotSaveException
    {
        if (history.id == null) {
            this.create(history);
            return;
        }

        try (Connection connection = this.getConnection()) {
            String sqlStatement =
                    "UPDATE History SET timestamp = ?, currentLoggedIn = ?, loginsPerSixHours = ?, registrationsPerSixHours = ?, "
                    + "postsPerMinute = ?, requestsPerSecond = ?, requestsLoggedInPerSecond = ?, currentlyRegisteredUsers = ?, "
                    + "trendingHashtags = ?, systemLoadAverage = ?, systemCPULoad = ?, processCPULoad = ?, "
                    + "memoryMax = ?, memoryTotal = ?, memoryFree = ?, memoryUsed = ?, threadCount = ?, threadCountTotal = ? "
                    + "WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sqlStatement);

            statement.setTimestamp(1, this.getSQLTimestampFromLocalDateTime(history.timestamp));
            statement.setInt(2, history.currentLoggedIn);
            statement.setFloat(3, history.loginsPerSixHours);
            statement.setFloat(4, history.registrationsPerSixHours);
            statement.setFloat(5, history.postsPerMinute);
            statement.setFloat(6, history.requestsPerSecond);
            statement.setFloat(7, history.requestsLoggedInPerSecond);
            statement.setInt(8, history.currentlyRegisteredUsers);
            statement.setString(9, history.getCommaSeperatedTags());
            statement.setDouble(10, history.systemLoadAverage);
            statement.setDouble(11, history.systemCPULoad);
            statement.setDouble(12, history.processCPULoad);
            statement.setInt(13, history.memoryMax);
            statement.setInt(14, history.memoryTotal);
            statement.setInt(15, history.memoryFree);
            statement.setInt(16, history.memoryUsed);
            statement.setInt(17, history.threadCount);
            statement.setInt(18, history.threadCountTotal);
            statement.setLong(19, history.id);

            int rowCount = statement.executeUpdate();

            statement.close();

            if (rowCount != 1) {
                throw new CouldNotSaveException("History");
            }
        } catch (SQLException e) {
            //this.log("History.save(): " + e.getMessage());
            throw new CouldNotSaveException("History");
        }
    }

    @Override
    public void delete(History history) throws CouldNotDeleteException
    {
        if (history.id == null) {
            throw new CouldNotDeleteException("History with ID null");
        }

        try (Connection connection = this.getConnection()) {
            String            sqlStatement = "delete from History where id = ?";
            PreparedStatement statement    = connection.prepareStatement(sqlStatement);

            statement.setLong(1, history.id);

            int rowCount = statement.executeUpdate();

            statement.close();

            if (rowCount == 0) {
                throw new CouldNotDeleteException("History with ID " + history.id);
            }
        } catch (SQLException e) {
            //this.log("History.delete(): " + e.getMessage());
            throw new CouldNotDeleteException("History with ID " + history.id);
        }
    }

    private History mapResultSetToHistory(ResultSet resultSet) throws SQLException
    {
        History history = (History) ObjectManager.create(History.class);

        history.id                        = resultSet.getLong("id");
        history.timestamp                 = this.getLocalDateTimeFromSQLTimestamp(resultSet.getTimestamp("timestamp"));
        history.currentLoggedIn           = resultSet.getInt("currentLoggedIn");
        history.loginsPerSixHours         = resultSet.getFloat("loginsPerSixHours");
        history.registrationsPerSixHours  = resultSet.getFloat("registrationsPerSixHours");
        history.postsPerMinute            = resultSet.getFloat("postsPerMinute");
        history.requestsPerSecond         = resultSet.getFloat("requestsPerSecond");
        history.requestsLoggedInPerSecond = resultSet.getFloat("requestsLoggedInPerSecond");
        history.currentlyRegisteredUsers  = resultSet.getInt("currentlyRegisteredUsers");
        history.trendingHashtags          = History.getTrendingHashtagsFromCommaSeparatedTags(resultSet.getString(
                "trendingHashtags"));
        history.systemLoadAverage         = resultSet.getDouble("systemLoadAverage");
        history.systemCPULoad             = resultSet.getDouble("systemCPULoad");
        history.processCPULoad            = resultSet.getDouble("processCPULoad");
        history.memoryMax                 = resultSet.getInt("memoryMax");
        history.memoryTotal               = resultSet.getInt("memoryTotal");
        history.memoryFree                = resultSet.getInt("memoryFree");
        history.memoryUsed                = resultSet.getInt("memoryUsed");
        history.threadCount               = resultSet.getInt("threadCount");
        history.threadCountTotal          = resultSet.getInt("threadCountTotal");

        return history;
    }
}
