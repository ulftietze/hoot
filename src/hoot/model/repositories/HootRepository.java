package hoot.model.repositories;

import hoot.model.entities.*;
import hoot.model.search.SearchCriteriaInterface;
import hoot.system.Exception.CouldNotDeleteException;
import hoot.system.Exception.CouldNotSaveException;
import hoot.system.Exception.EntityNotFoundException;
import hoot.system.ObjectManager.ObjectManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class HootRepository extends AbstractRepository<Hoot>
{
    public Hoot getById(int id)
    {
        // TODO: Also include COUNT of Reactions
        return null;
    }

    @Override
    public ArrayList<Hoot> getList(SearchCriteriaInterface searchCriteria) throws EntityNotFoundException
    {
        try {
            Connection        connection = this.getConnection();
            PreparedStatement statement  = searchCriteria.getQueryStatement(connection);
            ResultSet         resultSet  = statement.executeQuery();

            ArrayList<Hoot> allHoots = new ArrayList<>();
            UserRepository userRepository = (UserRepository) ObjectManager.get(UserRepository.class);

            while (resultSet.next()) {
                String DBhootType = resultSet.getString("h.hootType");

                switch (HootType.valueOf(DBhootType)) {
                    // yes, this is bad, but it just works (TM)
                    case Image:
                        Image imageHoot = new Image();
                        imageHoot.created      = this.getLocalDateTimeFromSQLTimestamp(resultSet.getTimestamp("h.created"));
                        imageHoot.imagePath    = resultSet.getString("i.imagePath");
                        imageHoot.content      = resultSet.getString("i.content");
                        imageHoot.onlyFollower = resultSet.getBoolean("i.onlyFollower");
                        imageHoot.id           = resultSet.getInt("h.id");
                        imageHoot.user         = userRepository.getById(resultSet.getInt("h.user"));
                        allHoots.add(imageHoot);
                        break;
                    case Post:
                        Post postHoot = new Post();
                        postHoot.created      = this.getLocalDateTimeFromSQLTimestamp(resultSet.getTimestamp("h.created"));
                        postHoot.content      = resultSet.getString("i.content");
                        postHoot.onlyFollower = resultSet.getBoolean("i.onlyFollower");
                        postHoot.id           = resultSet.getInt("h.id");
                        postHoot.user         = userRepository.getById(resultSet.getInt("h.user"));
                        allHoots.add(postHoot);
                        break;
                    case Comment:
                        Comment commentHoot = new Comment();
                        commentHoot.created = this.getLocalDateTimeFromSQLTimestamp(resultSet.getTimestamp("h.created"));
                        commentHoot.content = resultSet.getString("i.content");
                        commentHoot.id      = resultSet.getInt("h.id");
                        commentHoot.user    = userRepository.getById(resultSet.getInt("h.user"));
                        commentHoot.parent  = this.getById(resultSet.getInt("c.parent"));
                        allHoots.add(commentHoot);
                        break;
                    default:
                        this.log("HootType from DB was not found in Java entity enum.");
                        throw new EntityNotFoundException("HootType");
                }
            }

            resultSet.close();
            statement.close();
            connection.close();

            return allHoots;
        } catch (SQLException e) {
            this.log("Could not load list of Hoots: " + e.getMessage());
            throw new EntityNotFoundException("HootList");
        }
    }

    @Override
    public void save(Hoot hoot) throws CouldNotSaveException
    {
        // save parent hoot info

        switch (hoot.hootType) {
            case Post:
                // save post data
                Post postHoot = (Post) hoot;
                break;
            case Image:
                // save image data
                Image imageHoot = (Image) hoot;
                break;
            case Comment:
                // save comment data
                Comment commentHoot = (Comment) hoot;
                break;
        }
    }

    @Override
    public void delete(Hoot hoot) throws CouldNotDeleteException
    {
        // delte parent hoot ID (CASCADE delete in DB)
    }
}
