package hoot.model.repositories;

import hoot.model.entities.HootMentions;
import hoot.model.entities.Mention;
import hoot.model.search.SearchCriteriaInterface;
import hoot.system.Exception.CouldNotDeleteException;
import hoot.system.Exception.CouldNotSaveException;
import hoot.system.Exception.EntityNotFoundException;
import hoot.system.Logger.QueryLoggerInterface;
import hoot.system.objects.ObjectManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class HootMentionRepository extends AbstractRepository<HootMentions>
{
    /**
     * This method never makes sense. Use MentionRepository.getList() instead.
     */
    @Override
    public ArrayList<HootMentions> getList(SearchCriteriaInterface searchCriteria) throws EntityNotFoundException
    {
        return null;
    }

    @Override
    public void save(HootMentions hootMentions) throws CouldNotSaveException
    {
        if (hootMentions.hoot == null) {
            throw new CouldNotSaveException("HootMentions for null Hoot");
        }

        try (Connection connection = this.getConnection()) {
            this.delete(hootMentions);

            if (hootMentions.mentions.size() == 0) {
                return;
            }

            ArrayList<Integer> parameters = new ArrayList<>();

            String statement = "INSERT INTO HootMentions (hoot, mention) VALUES ";
            statement += hootMentions.mentions.stream().map(mention -> "(?,?)").collect(Collectors.joining(","));

            PreparedStatement pss = connection.prepareStatement(statement);

            for (Mention mention : hootMentions.mentions) {
                parameters.add(hootMentions.hoot.id);
                parameters.add(mention.mentioned.id);
            }

            for (int i = 1; i <= parameters.size(); i++) {
                pss.setInt(i, parameters.get(i - 1));
            }

            QueryLoggerInterface logger = (QueryLoggerInterface) ObjectManager.get(QueryLoggerInterface.class);
            logger.log(statement + " [parameters=" + parameters + "]");

            this.statementFetcher.executeUpdate(pss);
            connection.close();
        } catch (SQLException e) {
            this.log(e.getMessage());
            throw new CouldNotSaveException("HootMentions for Hoot " + hootMentions.hoot.id);
        }
    }

    @Override
    public void delete(HootMentions hootMentions) throws CouldNotDeleteException
    {
        if (hootMentions.hoot == null) {
            throw new CouldNotDeleteException("HootMentions for null Hoot");
        }

        try (Connection connection = this.getConnection()) {
            String            statement = "delete from HootMentions where hoot = ?";
            PreparedStatement pss       = connection.prepareStatement(statement);
            pss.setInt(1, hootMentions.hoot.id);

            this.statementFetcher.executeUpdate(pss);
        } catch (SQLException e) {
            this.log(e.getMessage());
            throw new CouldNotDeleteException("HootMentions for Hoot " + hootMentions.hoot.id);
        }
    }
}
