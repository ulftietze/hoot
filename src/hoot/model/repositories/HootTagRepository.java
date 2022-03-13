package hoot.model.repositories;

import hoot.model.entities.HootTags;
import hoot.model.entities.Tag;
import hoot.model.search.SearchCriteriaInterface;
import hoot.system.Exception.CouldNotDeleteException;
import hoot.system.Exception.CouldNotSaveException;
import hoot.system.Exception.EntityNotFoundException;
import hoot.system.Logger.QueryLoggerInterface;
import hoot.system.ObjectManager.ObjectManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class HootTagRepository extends AbstractRepository<HootTags>
{
    private final TagRepository tagRepository;

    public HootTagRepository()
    {
        super();

        this.tagRepository = (TagRepository) ObjectManager.get(TagRepository.class);
    }

    /**
     * This method never makes sense. Use TagRepository.getList() instead.
     */
    @Override
    public ArrayList<HootTags> getList(SearchCriteriaInterface searchCriteria) throws EntityNotFoundException
    {
        return null;
    }

    @Override
    public void save(HootTags hootTags) throws CouldNotSaveException
    {
        if (hootTags.hoot == null) {
            throw new CouldNotSaveException("HootTags for null Hoot");
        }

        try (Connection connection = this.getConnection()) {
            this.delete(hootTags);

            if (hootTags.tags.isEmpty()) {
                return;
            }

            ArrayList<String> parameters = new ArrayList<>();

            String statement = "INSERT INTO HootTags (hoot, tag) VALUES ";
            statement += hootTags.tags.stream().map(tag -> "(?,?)").collect(Collectors.joining(","));

            for (Tag tag : hootTags.tags) {
                tagRepository.save(tag);
                parameters.add(hootTags.hoot.id.toString());
                parameters.add(tag.tag);
            }

            PreparedStatement pss = connection.prepareStatement(statement);

            for (int i = 1; i <= parameters.size(); i++) {
                pss.setString(i, parameters.get(i - 1));
            }

            QueryLoggerInterface logger = (QueryLoggerInterface) ObjectManager.get(QueryLoggerInterface.class);
            logger.log(statement + " [parameters=" + parameters + "]");

            this.statementFetcher.executeUpdate(pss);
        } catch (SQLException e) {
            this.log(e.getMessage());
            throw new CouldNotSaveException("HootTag for Hoot " + hootTags.hoot.id);
        }
    }

    @Override
    public void delete(HootTags hootTags) throws CouldNotDeleteException
    {
        if (hootTags.hoot == null) {
            throw new CouldNotDeleteException("HootTags for null Hoot");
        }

        try (Connection connection = this.getConnection()) {
            String            statement = "DELETE FROM HootTags where hoot = ?";
            PreparedStatement pss       = connection.prepareStatement(statement);
            pss.setInt(1, hootTags.hoot.id);

            this.statementFetcher.executeUpdate(pss);
        } catch (SQLException e) {
            this.log(e.getMessage());
            throw new CouldNotDeleteException("HootTag for Hoot " + hootTags.hoot.id);
        }
    }
}
