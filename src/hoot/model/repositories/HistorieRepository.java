package hoot.model.repositories;

import hoot.model.entities.Historie;
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

public class HistorieRepository extends AbstractRepository<Historie>
{
    public Historie getById(Long id) throws EntityNotFoundException
    {
        try {
            Connection connection = this.getConnection();

            QueryBuilder queryBuilder = (QueryBuilder) ObjectManager.create(QueryBuilder.class);
            queryBuilder.SELECT.add("*");
            queryBuilder.FROM = "Historie";
            queryBuilder.WHERE.add("id = ?");
            queryBuilder.PARAMETERS.add(id);

            PreparedStatement statement = queryBuilder.build(connection);

            ResultSet resultSet = statement.executeQuery();
            resultSet.next();

            Historie historie = this.mapResultSetToHistorie(resultSet);

            resultSet.close();
            statement.close();
            connection.close();

            return historie;
        } catch (SQLException e) {
            this.log("HistorieRepository.getById(): " + e.getSQLState() + " " + e.getMessage());
            throw new EntityNotFoundException("Historie");
        }
    }

    @Override
    public ArrayList<Historie> getList(SearchCriteriaInterface searchCriteria) throws EntityNotFoundException
    {
        ArrayList<Historie> allHistories = new ArrayList<>();

        try {
            Connection connection = this.getConnection();

            QueryBuilder queryBuilder = searchCriteria.getQueryBuilder();
            queryBuilder.FROM = "Historie";

            PreparedStatement statement = queryBuilder.build(connection);

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                allHistories.add(this.mapResultSetToHistorie(resultSet));
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            this.log("HistorieRepository.getList(): " + e.getMessage());
            throw new EntityNotFoundException("Historie");
        }

        return allHistories;
    }

    @Override
    public void save(Historie historie) throws CouldNotSaveException
    {
        try {
            Connection connection = this.getConnection();

            String
                    sqlStatement
                    = "insert into Historie (currentLoggedIn, postsPerSecond, requestsPerSecond, loginsPerSecond, currentlyRegisteredUsers, trendingHashtags) values (?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(
                    sqlStatement,
                    PreparedStatement.RETURN_GENERATED_KEYS
            );

            statement.setInt(1, historie.currentLoggedIn);
            statement.setFloat(2, historie.postsPerSecond);
            statement.setFloat(3, historie.requestsPerSecond);
            statement.setFloat(4, historie.loginsPerSecond);
            statement.setInt(5, historie.currentlyRegisteredUsers);
            statement.setString(6, historie.getCommaSeperatedTags());

            int       rowCount  = statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();

            resultSet.next();
            historie.id = resultSet.getLong(1);

            resultSet.close();
            statement.close();
            connection.close();

            if (rowCount == 0) {
                throw new CouldNotSaveException("Historie");
            }
        } catch (SQLException e) {
            this.log("Historie.save()" + e.getMessage());
        }
    }

    @Override
    public void delete(Historie historie) throws CouldNotDeleteException
    {
        if (historie.id == null) {
            throw new CouldNotDeleteException("Historie with ID null");
        }

        try {
            Connection connection = this.getConnection();

            String            sqlStatement = "delete from Historie where id = ?";
            PreparedStatement statement    = connection.prepareStatement(sqlStatement);

            statement.setLong(1, historie.id);

            int rowCount = statement.executeUpdate();

            statement.close();
            connection.close();

            if (rowCount == 0) {
                throw new CouldNotDeleteException("Historie with ID " + historie.id);
            }
        } catch (SQLException e) {
            this.log("Historie.delete(): " + e.getMessage());
        }
    }

    private Historie mapResultSetToHistorie(ResultSet resultSet) throws SQLException
    {
        Historie historie = (Historie) ObjectManager.create(Historie.class);

        historie.id                       = resultSet.getLong("id");
        historie.timestamp                = this.getLocalDateTimeFromSQLTimestamp(resultSet.getTimestamp("timestamp"));
        historie.currentLoggedIn          = resultSet.getInt("currentLoggedIn");
        historie.postsPerSecond           = resultSet.getFloat("postsPerSecond");
        historie.requestsPerSecond        = resultSet.getFloat("requestsPerSecond");
        historie.loginsPerSecond          = resultSet.getFloat("loginsPerSecond");
        historie.currentlyRegisteredUsers = resultSet.getInt("currentlyRegisteredUsers");
        historie.trendingHashtags         = historie.getTrendingHashtagsFromCommaSeparatedTags(resultSet.getString("trendingHashtags"));

        return historie;
    }
}
