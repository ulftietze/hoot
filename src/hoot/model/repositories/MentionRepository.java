package hoot.model.repositories;

import hoot.model.entities.Mention;
import hoot.model.search.SearchCriteriaInterface;
import hoot.system.Database.QueryBuilder;
import hoot.system.Database.QueryResult;
import hoot.system.Database.QueryResultRow;
import hoot.system.Exception.CouldNotDeleteException;
import hoot.system.Exception.CouldNotSaveException;
import hoot.system.Exception.EntityNotFoundException;
import hoot.system.objects.Inject;
import hoot.system.objects.ObjectManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class MentionRepository extends AbstractRepository<Mention>
{
    @Inject private UserRepository userRepository;

    @Override
    public ArrayList<Mention> getList(SearchCriteriaInterface searchCriteria) throws EntityNotFoundException
    {
        ArrayList<Mention> mentioned = new ArrayList<>();

        try (Connection connection = this.getConnection()) {
            QueryBuilder queryBuilder = searchCriteria.getQueryBuilder();
            queryBuilder.SELECT.add("mention");
            queryBuilder.FROM = "HootMentions";

            PreparedStatement statement   = queryBuilder.build(connection);
            QueryResult       queryResult = this.statementFetcher.fetchAll(statement);
            connection.close();

            for (QueryResultRow row : queryResult) {
                Mention mention = new Mention();
                mention.mentioned = this.userRepository.getById((int) row.get("HootMentions.mention"));
                mentioned.add(mention);
            }
        } catch (SQLException e) {
            this.log("MentionRepository.getList(): " + e.getMessage());
        }

        return mentioned;
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
