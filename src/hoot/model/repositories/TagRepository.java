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

    }
}
