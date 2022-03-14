package hoot.model.repositories;

import hoot.model.entities.Follower;
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
import java.util.ArrayList;
import java.util.Objects;

public class FollowerRepository extends AbstractRepository<Follower>
{
    public Long getFollowerCountForUser(Integer userID) throws EntityNotFoundException
    {
        if (userID == null) {
            throw new EntityNotFoundException("FollowerCount for User with empty ID");
        }

        Long followerCount = 0L;

        try (Connection connection = this.getConnection()) {
            QueryBuilder queryBuilder = (QueryBuilder) ObjectManager.get(QueryBuilder.class, true);

            queryBuilder.SELECT.add("COUNT(user) as quantity");
            queryBuilder.FROM = "Follower";
            queryBuilder.WHERE.add("follows = ?");
            queryBuilder.PARAMETERS.add(userID.toString());

            PreparedStatement preparedStatement = queryBuilder.build(connection);

            return (Long) this.statementFetcher.fetchOne(preparedStatement).get("quantity");
        } catch (SQLException e) {
            this.log("FollowerCount could not be retrieved: " + e.getMessage());
        }

        return followerCount;
    }

    public Long getFollowsCountForUser(Integer userID) throws EntityNotFoundException
    {
        if (userID == null) {
            throw new EntityNotFoundException("FollowsCount for User with empty ID");
        }

        Long followsCount = 0L;

        try (Connection connection = this.getConnection()) {
            QueryBuilder queryBuilder = (QueryBuilder) ObjectManager.get(QueryBuilder.class, true);

            queryBuilder.SELECT.add("COUNT(user) as quantity");
            queryBuilder.FROM = "Follower";
            queryBuilder.WHERE.add("user = ?");
            queryBuilder.PARAMETERS.add(userID.toString());

            PreparedStatement preparedStatement = queryBuilder.build(connection);

            return (Long) this.statementFetcher.fetchOne(preparedStatement).get("quantity");
        } catch (SQLException e) {
            this.log("FollowerCount could not be retrieved: " + e.getMessage());
        }

        return followsCount;
    }

    @Override
    public ArrayList<Follower> getList(SearchCriteriaInterface searchCriteria) throws EntityNotFoundException
    {
        ArrayList<Follower> followerList = new ArrayList<>();

        try (Connection connection = this.getConnection()) {
            QueryBuilder queryBuilder = searchCriteria.getQueryBuilder();
            queryBuilder.SELECT.add("*");
            queryBuilder.FROM = "Follower";
            PreparedStatement statement = queryBuilder.build(connection);

            QueryResult queryResult = this.statementFetcher.fetchAll(statement);
            connection.close();

            UserRepository userRepository = (UserRepository) ObjectManager.get(UserRepository.class);

            for (QueryResultRow row : queryResult) {
                Follower follower = (Follower) ObjectManager.create(Follower.class);

                follower.user    = userRepository.getById((Integer) row.get("Follower.user"));
                follower.follows = userRepository.getById((Integer) row.get("Follower.follows"));

                followerList.add(follower);
            }
        } catch (SQLException e) {
            this.log("FollowerRepository.getList(): " + e.getMessage());
        }

        return followerList;
    }

    @Override
    public void save(Follower entity) throws CouldNotSaveException
    {
        if (entity.user == null || entity.follows == null) {
            throw new CouldNotSaveException("Follower (ID null)");
        }

        if (Objects.equals(entity.user.id, entity.follows.id)) {
            throw new CouldNotSaveException("Follower can't follows itself.");
        }

        try (Connection connection = this.getConnection()) {
            String sqlStatement = "INSERT INTO Follower (user, follows) VALUES (?, ?)";

            PreparedStatement pss = connection.prepareStatement(sqlStatement);
            pss.setInt(1, entity.user.id);
            pss.setInt(2, entity.follows.id);

            int rowCount = this.statementFetcher.executeUpdate(pss);
            connection.close();

            if (rowCount == 0) {
                throw new CouldNotSaveException("Follower");
            }
        } catch (SQLException e) {
            String msg = "New Follower " + entity.user.id + " trying to follow " + entity.follows.id + e.getMessage();
            this.log(msg);
            throw new CouldNotSaveException(msg);
        }
    }

    @Override
    public void delete(Follower entity) throws CouldNotDeleteException
    {
        if (entity.user.id == null || entity.follows.id == null) {
            throw new CouldNotDeleteException("Follower (ID null)");
        }

        try (Connection connection = this.getConnection()) {
            String            sqlStatement = "delete from Follower where user = ? and follows = ?";
            PreparedStatement pss          = connection.prepareStatement(sqlStatement);

            pss.setInt(1, entity.user.id);
            pss.setInt(2, entity.follows.id);

            int rowCount = this.statementFetcher.executeUpdate(pss);
            connection.close();

            if (rowCount == 0) {
                throw new CouldNotDeleteException("Follower");
            }
        } catch (SQLException e) {
            this.log(e.getMessage());
            throw new CouldNotDeleteException("Delete Follower " + entity.user.id + " following " + entity.follows.id);
        }
    }
}
