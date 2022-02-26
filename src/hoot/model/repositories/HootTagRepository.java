package hoot.model.repositories;

import hoot.model.entities.HootTags;
import hoot.model.entities.Tag;
import hoot.model.search.SearchCriteriaInterface;
import hoot.system.Exception.CouldNotDeleteException;
import hoot.system.Exception.CouldNotSaveException;
import hoot.system.Exception.EntityNotFoundException;
import hoot.system.ObjectManager.ObjectManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class HootTagRepository extends AbstractRepository<HootTags>
{

    @Override
    public ArrayList<HootTags> getList(SearchCriteriaInterface searchCriteria) throws EntityNotFoundException
    {
        // this method never makes sense. Use TagRepository.getList() instead.
        return null;
    }

    @Override
    public void save(HootTags hootTags) throws CouldNotSaveException
    {
        if (hootTags.hoot == null) {
            throw new CouldNotSaveException("HootTags for null Hoot");
        }

        try {
            Connection connection = this.getConnection();

            TagRepository tagRepository = (TagRepository) ObjectManager.get(TagRepository.class);

            for (Tag tag : hootTags.tags) {
                try {
                    tagRepository.save(tag);

                    String statement = "insert into HootTags (hoot, tag) VALUES (?, ?)";
                    PreparedStatement pss = connection.prepareStatement(statement);

                    pss.setInt(1, hootTags.hoot.id);
                    pss.setString(2, tag.tag);

                    pss.executeUpdate();
                    pss.close();
                } catch (CouldNotSaveException | SQLException ignore) {}
            }

            connection.close();
        } catch (SQLException e) {
            this.log(e.getMessage());
        }
    }

    @Override
    public void delete(HootTags hootTags) throws CouldNotDeleteException
    {

    }
}
