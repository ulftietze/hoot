package hoot.model.repositories;

import hoot.model.entities.Tag;
import hoot.model.search.SearchCriteriaInterface;
import hoot.system.Database.QueryBuilder;
import hoot.system.Exception.CouldNotDeleteException;
import hoot.system.Exception.CouldNotSaveException;
import hoot.system.Exception.EntityNotFoundException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class TagRepository extends AbstractRepository<Tag>
{
    @Override
    public ArrayList<Tag> getList(SearchCriteriaInterface searchCriteria) throws EntityNotFoundException
    {
        try {
            ArrayList<Tag> tags         = new ArrayList<>();
            QueryBuilder   queryBuilder = searchCriteria.getQueryBuilder();

            queryBuilder.SELECT.add("tag");
            queryBuilder.FROM = "HootTags";

            Connection        connection = this.getConnection();
            PreparedStatement statement  = queryBuilder.build(connection);
            ResultSet         resultSet  = statement.executeQuery();

            while (resultSet.next()) {
                Tag tag = new Tag();
                tag.tag = resultSet.getString("tag");
                tags.add(tag);
            }

            resultSet.close();
            statement.close();
            connection.close();

            return tags;
        } catch (SQLException e) {
            this.log("TagRepository.getList(): " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Insert a new Tag into the DB if it doesn't exist already
     *
     * @param tag
     * @throws CouldNotSaveException
     */
    @Override
    public void save(Tag tag) throws CouldNotSaveException
    {
        try {
            Connection        connection   = this.getConnection();
            String            sqlStatement = "INSERT INTO Tag values (?) ON DUPLICATE KEY UPDATE tag = tag";
            PreparedStatement statement    = connection.prepareStatement(sqlStatement);

            statement.setString(1, tag.tag.toLowerCase());

            int rowCount = statement.executeUpdate();

            /*  If the insert fails, we cannot determine if it failed because of an SQL error,
                or because the key already exists.
                For this Reason, we do not throw a CouldNotSaveException if the row count is not 1. */

            statement.close();
            connection.close();
        } catch (SQLException e) {
            this.log("Could not save Tag " + tag.tag);
            throw new CouldNotSaveException("Tag " + tag.tag);
        }
    }

    /**
     * Delete a Tag from the DB
     *
     * @param tag
     * @throws CouldNotDeleteException
     */
    @Override
    public void delete(Tag tag) throws CouldNotDeleteException
    {
        try {
            Connection        connection   = this.getConnection();
            String            sqlStatement = "delete from Tag where tag = ?";
            PreparedStatement statement    = connection.prepareStatement(sqlStatement);

            statement.setString(1, tag.tag.toLowerCase());

            int rowCount = statement.executeUpdate();

            statement.close();
            connection.close();

            if (rowCount == 0) {
                throw new CouldNotDeleteException("Tag " + tag.tag);
            }
        } catch (SQLException e) {
            this.log("Could not delete Tag " + tag.tag);
            throw new CouldNotDeleteException("Tag " + tag.tag);
        }
    }
}
