package hoot.model.repositories;

import hoot.model.entities.History;
import hoot.model.search.SearchCriteriaInterface;
import hoot.system.Database.QueryBuilder;
import hoot.system.Database.QueryResult;
import hoot.system.Database.QueryResultRow;
import hoot.system.Exception.CouldNotDeleteException;
import hoot.system.Exception.CouldNotSaveException;
import hoot.system.Exception.EntityNotFoundException;
import hoot.system.Monitoring.CollectorResult;
import hoot.system.objects.Inject;
import hoot.system.objects.ObjectManager;
import hoot.system.Serializer.Serializer;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

public class HistoryRepository extends AbstractRepository<History>
{
    @Inject private Serializer serializer;

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
            QueryResultRow    resultRow = this.statementFetcher.fetchOne(statement);
            connection.close();

            history = this.mapResultSetToHistory(resultRow);
        } catch (SQLException e) {
            //this.log("HistoryRepository.getById(): " + e.getSQLState() + " " + e.getMessage());
            throw new EntityNotFoundException("History");
        }

        return history;
    }

    @Override
    public ArrayList<History> getList(SearchCriteriaInterface searchCriteria) throws EntityNotFoundException
    {
        ArrayList<History> historyList = new ArrayList<>();

        try (Connection connection = this.getConnection()) {
            QueryBuilder queryBuilder = searchCriteria.getQueryBuilder();
            queryBuilder.SELECT.add("*");
            queryBuilder.FROM = "History";

            PreparedStatement statement = queryBuilder.build(connection);

            QueryResult queryResult = this.statementFetcher.fetchAll(statement);
            connection.close();

            for (QueryResultRow resultRow : queryResult) {
                historyList.add(this.mapResultSetToHistory(resultRow));
            }
        } catch (SQLException e) {
            //this.log("HistoryRepository.getList(): " + e.getMessage());
            throw new EntityNotFoundException("History");
        }

        return historyList;
    }

    public void create(History history) throws CouldNotSaveException
    {
        try (Connection connection = this.getConnection()) {
            String sqlStatement = "INSERT INTO History (currentLoggedIn, loginsPerSixHours, registrationsPerSixHours, "
                                  + "postsPerMinute, requestsPerSecond, requestsLoggedInPerSecond, currentlyRegisteredUsers, "
                                  + "trendingHashtags, systemLoadAverage, systemCPULoad, processCPULoad, threadCount, "
                                  + "threadCountTotal, workload, queueSize, cacheSize, requestDurations) "
                                  + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) "
                                  + "RETURNING id, timestamp";

            PreparedStatement statement = connection.prepareStatement(sqlStatement);

            statement.setInt(1, history.currentLoggedIn);
            statement.setInt(2, history.loginsPerSixHours);
            statement.setLong(3, history.registrationsPerSixHours);
            statement.setFloat(4, history.postsPerMinute);
            statement.setInt(5, history.requestsPerSecond);
            statement.setFloat(6, history.requestsLoggedInPerSecond);
            statement.setLong(7, history.currentlyRegisteredUsers);
            statement.setString(8, history.getCommaSeperatedTags());
            statement.setDouble(9, history.systemLoadAverage);
            statement.setDouble(10, history.systemCPULoad);
            statement.setDouble(11, history.processCPULoad);
            statement.setInt(12, history.threadCount);
            statement.setLong(13, history.threadCountTotal);
            statement.setString(14, this.serializer.serialize(history.workload));
            statement.setString(15, this.serializer.serialize(history.queueSize));
            statement.setString(16, this.serializer.serialize(history.cacheSize));
            statement.setString(17, this.serializer.serialize(history.requestDurations));

            ResultSet resultSet = statement.executeQuery();

            resultSet.next();

            history.id        = resultSet.getLong(1);
            history.timestamp = this.getLocalDateTimeFromSQLTimestamp(resultSet.getTimestamp(2));

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            throw new CouldNotSaveException("History " + e.getMessage());
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
                    + "threadCount = ?, threadCountTotal = ?, workload = ?, queueSize = ?, cacheSize = ?, requestDurations = ? "
                    + "WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(sqlStatement);

            statement.setTimestamp(1, this.getSQLTimestampFromLocalDateTime(history.timestamp));
            statement.setInt(2, history.currentLoggedIn);
            statement.setLong(3, history.loginsPerSixHours);
            statement.setLong(4, history.registrationsPerSixHours);
            statement.setFloat(5, history.postsPerMinute);
            statement.setInt(6, history.requestsPerSecond);
            statement.setFloat(7, history.requestsLoggedInPerSecond);
            statement.setLong(8, history.currentlyRegisteredUsers);
            statement.setString(9, history.getCommaSeperatedTags());
            statement.setDouble(10, history.systemLoadAverage);
            statement.setDouble(11, history.systemCPULoad);
            statement.setDouble(12, history.processCPULoad);
            statement.setInt(13, history.threadCount);
            statement.setLong(14, history.threadCountTotal);
            statement.setString(15, this.serializer.serialize(history.workload));
            statement.setString(16, this.serializer.serialize(history.queueSize));
            statement.setString(17, this.serializer.serialize(history.cacheSize));
            statement.setString(18, this.serializer.serialize(history.requestDurations));
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

    private History mapResultSetToHistory(QueryResultRow resultRow) throws SQLException
    {
        History history = (History) ObjectManager.create(History.class);

        Timestamp timestamp        = (Timestamp) resultRow.get("History.timestamp");
        String    trendingHashtags = (String) resultRow.get("History.trendingHashtags");

        history.id                        = (Long) resultRow.get("History.id");
        history.timestamp                 = this.getLocalDateTimeFromSQLTimestamp(timestamp);
        history.currentLoggedIn           = (int) resultRow.get("History.currentLoggedIn");
        history.loginsPerSixHours         = (Integer) resultRow.get("History.loginsPerSixHours");
        history.registrationsPerSixHours  = (Long) resultRow.get("History.registrationsPerSixHours");
        history.postsPerMinute            = (float) resultRow.get("History.postsPerMinute");
        history.trendingHashtags          = History.getTrendingHashtagsFromCommaSeparatedTags(trendingHashtags);
        history.requestsPerSecond         = (int) resultRow.get("History.requestsPerSecond");
        history.requestsLoggedInPerSecond = (float) resultRow.get("History.requestsLoggedInPerSecond");
        history.currentlyRegisteredUsers  = (Long) resultRow.get("History.currentlyRegisteredUsers");
        history.systemLoadAverage         = (double) resultRow.get("History.systemLoadAverage");
        history.systemCPULoad             = (double) resultRow.get("History.systemCPULoad");
        history.processCPULoad            = (double) resultRow.get("History.processCPULoad");
        history.threadCount               = (int) resultRow.get("History.threadCount");
        history.threadCountTotal          = (Long) resultRow.get("History.threadCountTotal");

        String workload  = (String) resultRow.get("History.workload");
        String queueSize = (String) resultRow.get("History.queueSize");
        String cacheSize = (String) resultRow.get("History.cacheSize");
        String durations = (String) resultRow.get("History.requestDurations");

        try {
            history.workload         = (CollectorResult) this.serializer.deserialize(workload, CollectorResult.class);
            history.queueSize        = (CollectorResult) this.serializer.deserialize(queueSize, CollectorResult.class);
            history.cacheSize        = (CollectorResult) this.serializer.deserialize(cacheSize, CollectorResult.class);
            history.requestDurations = (CollectorResult) this.serializer.deserialize(durations, CollectorResult.class);
        } catch (IOException e) {
            this.log("Could not deserialize serialized history row: " + e.getMessage());
        }

        return history;
    }
}
