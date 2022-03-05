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

            resultSet.close();
            preparedStatement.close();
            connection.close();

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
        ArrayList<Follower> followerList = new ArrayList<>();

        try {
            QueryBuilder queryBuilder = searchCriteria.getQueryBuilder();

            queryBuilder.SELECT.add("*");
            queryBuilder.FROM = "Follower";

            Connection        connection = this.getConnection();
            PreparedStatement statement  = queryBuilder.build(connection);
            ResultSet         resultSet  = statement.executeQuery();

            UserRepository userRepository = (UserRepository) ObjectManager.get(UserRepository.class);

            while (resultSet.next()) {
                Follower follower = new Follower();
                follower.user    = userRepository.getById(resultSet.getInt("user"));
                follower.follows = userRepository.getById(resultSet.getInt("follows"));
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
        if (entity.user == null || entity.follows == null) {
            throw new CouldNotSaveException("Follower (ID null)");
        }

        try {
            Connection        connection   = this.getConnection();
            String            sqlStatement = "insert into Follower (user, follows) values (?, ?)";
            PreparedStatement pss          = connection.prepareStatement(sqlStatement);

            pss.setInt(1, entity.user.id);
            pss.setInt(2, entity.follows.id);

            int rowCount = pss.executeUpdate();

            pss.close();
            connection.close();

            if (rowCount == 0) {
                throw new CouldNotSaveException(
                        "New Follower " + entity.user.id + " trying to follow " + entity.follows.id);
            }
        } catch (SQLException e) {
            this.log(e.getMessage());
            throw new CouldNotSaveException("New Follower " + entity.user.id + " trying to follow " + entity.follows.id);
        }
    }

    @Override
    public void delete(Follower entity) throws CouldNotDeleteException
    {
        if (entity.user.id == null || entity.follows.id == null) {
            throw new CouldNotDeleteException("Follower (ID null)");
        }

        try {
            Connection        connection   = this.getConnection();
            String            sqlStatement = "delete from Follower where user = ? and follows = ?";
            PreparedStatement pss          = connection.prepareStatement(sqlStatement);

            pss.setInt(1, entity.user.id);
            pss.setInt(2, entity.follows.id);

            int rowCount = pss.executeUpdate();

            pss.close();
            connection.close();

            if (rowCount == 0) {
                throw new CouldNotDeleteException(
                        "Delete Follower " + entity.user.id + " following " + entity.follows.id);
            }
        } catch (SQLException e) {
            this.log(e.getMessage());
            throw new CouldNotDeleteException("Delete Follower " + entity.user.id + " following " + entity.follows.id);
        }
    }
}
