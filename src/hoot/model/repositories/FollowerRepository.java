package hoot.model.repositories;

import hoot.model.entities.Follower;
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

public class FollowerRepository extends AbstractRepository<Follower>
{
    public int getFollowerCountForUser(Integer userID) throws EntityNotFoundException
    {
        if (userID == null) {
            throw new EntityNotFoundException("FollowerCount for User with empty ID");
        }

        int followerCount = 0;

        try {
            QueryBuilder queryBuilder = (QueryBuilder) ObjectManager.get(QueryBuilder.class, true);

            queryBuilder.SELECT.add("COUNT(user)");
            queryBuilder.FROM = "Follower";
            queryBuilder.WHERE.add("follows = ?");
            queryBuilder.PARAMETERS.add(userID.toString());

            Connection        connection        = this.getConnection();
            PreparedStatement preparedStatement = queryBuilder.build(connection);
            ResultSet         resultSet         = preparedStatement.executeQuery();

            resultSet.next();
            followerCount = resultSet.getInt(1);
        } catch (SQLException e) {
            this.log("FollowerCount could not be retrieved: " + e.getMessage());
        }

        return followerCount;
    }

    @Override
    public ArrayList<Follower> getList(SearchCriteriaInterface searchCriteria) throws EntityNotFoundException
    {
        // TODO: limit to 50 with FollowerToUser SearchCriteria
        ArrayList<Follower> followerList = new ArrayList<>();

        try {
            QueryBuilder queryBuilder = searchCriteria.getQueryBuilder();
            queryBuilder.FROM = "Follower";

            Connection        connection = this.getConnection();
            PreparedStatement statement  = queryBuilder.build(connection);
            ResultSet         resultSet  = statement.executeQuery();

            while (resultSet.next()) {
                Follower follower = new Follower();
                follower.userID    = resultSet.getInt("user");
                follower.followsID = resultSet.getInt("follows");
                followerList.add(follower);
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            this.log("FollowerRepository.getList(): " + e.getMessage());
        }

        return followerList;
    }

    @Override
    public void save(Follower entity) throws CouldNotSaveException
    {
        if (entity.userID == null || entity.followsID == null) {
            throw new CouldNotSaveException("Follower (ID null)");
        }

        try {
            Connection        connection   = this.getConnection();
            String            sqlStatement = "insert into Follower (user, follows) values (?, ?)";
            PreparedStatement pss          = connection.prepareStatement(sqlStatement);

            pss.setInt(1, entity.userID);
            pss.setInt(2, entity.followsID);

            int rowCount = pss.executeUpdate();

            pss.close();
            connection.close();

            if (rowCount == 0) {
                throw new CouldNotSaveException(
                        "New Follower " + entity.userID + " trying to follow " + entity.followsID);
            }
        } catch (SQLException e) {
            this.log(e.getMessage());
            throw new CouldNotSaveException("New Follower " + entity.userID + " trying to follow " + entity.followsID);
        }
    }

    @Override
    public void delete(Follower entity) throws CouldNotDeleteException
    {
        if (entity.userID == null || entity.followsID == null) {
            throw new CouldNotDeleteException("Follower (ID null)");
        }

        try {
            Connection        connection   = this.getConnection();
            String            sqlStatement = "delete from Follower where user = ? and follows = ?";
            PreparedStatement pss          = connection.prepareStatement(sqlStatement);

            pss.setInt(1, entity.userID);
            pss.setInt(2, entity.followsID);

            int rowCount = pss.executeUpdate();

            pss.close();
            connection.close();

            if (rowCount == 0) {
                throw new CouldNotDeleteException(
                        "Delete Follower " + entity.userID + " following " + entity.followsID);
            }
        } catch (SQLException e) {
            this.log(e.getMessage());
            throw new CouldNotDeleteException("Delete Follower " + entity.userID + " following " + entity.followsID);
        }
    }
}
