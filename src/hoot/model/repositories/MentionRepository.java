package hoot.model.repositories;

import hoot.model.entities.Mention;
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

public class MentionRepository extends AbstractRepository<Mention>
{
    @Override
    public ArrayList<Mention> getList(SearchCriteriaInterface searchCriteria) throws EntityNotFoundException
    {
        try {
            ArrayList<Mention> mentioned    = new ArrayList<>();
            QueryBuilder       queryBuilder = searchCriteria.getQueryBuilder();

            Connection        connection = this.getConnection();
            PreparedStatement statement  = queryBuilder.build(connection);
            ResultSet         resultSet  = statement.executeQuery();

            UserRepository userRepository = (UserRepository) ObjectManager.get(UserRepository.class);

            while (resultSet.next()) {
                Mention mention = new Mention();
                mention.mentioned = userRepository.getById(resultSet.getInt("mention"));
                mentioned.add(mention);
            }

            resultSet.close();
            statement.close();
            connection.close();

            return mentioned;
        } catch (SQLException e) {
            this.log("MentionRepository.getList(): " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public void save(Mention mention) throws CouldNotSaveException
    {
        // TODO: This method is redundant. Mentions are updated in HootMentionRepository
    }

    @Override
    public void delete(Mention mention) throws CouldNotDeleteException
    {
        // TODO: This method is redundant. Mentions are updated in HootMentionRepository
    }
}
