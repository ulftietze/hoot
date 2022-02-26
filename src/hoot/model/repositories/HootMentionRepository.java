package hoot.model.repositories;

import hoot.model.entities.HootMentions;
import hoot.model.entities.Mention;
import hoot.model.search.SearchCriteriaInterface;
import hoot.system.Exception.CouldNotDeleteException;
import hoot.system.Exception.CouldNotSaveException;
import hoot.system.Exception.EntityNotFoundException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

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

        try {
            Connection connection = this.getConnection();

            for (Mention mention : hootMentions.mentions) {
                try {
                    String            statement = "insert into HootMentions (hoot, mention) VALUES (?, ?)";
                    PreparedStatement pss       = connection.prepareStatement(statement);

                    pss.setInt(1, hootMentions.hoot.id);
                    pss.setInt(2, mention.mentioned.id);

                    pss.executeUpdate();
                    pss.close();
                } catch (SQLException ignore) {
                }
            }

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

        try {
            Connection connection = this.getConnection();

            for (Mention mention : hootMentions.mentions) {
                try {
                    String            statement = "delete from HootMentions where hoot = ? and mention = ?";
                    PreparedStatement pss       = connection.prepareStatement(statement);

                    pss.setInt(1, hootMentions.hoot.id);
                    pss.setInt(2, mention.mentioned.id);

                    pss.executeUpdate();
                    pss.close();
                } catch (SQLException ignore) {
                }
            }

            connection.close();
        } catch (SQLException e) {
            this.log(e.getMessage());
            throw new CouldNotDeleteException("HootMentions for Hoot " + hootMentions.hoot.id);
        }
    }
}
