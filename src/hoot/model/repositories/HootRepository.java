package hoot.model.repositories;

import hoot.model.entities.*;
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
            // TODO:
            QueryBuilder queryBuilder = searchCriteria.getQueryBuilder();
            queryBuilder.SELECT.add("*");
            queryBuilder.FROM = "Hoot h";

            for (Interaction interaction: Interaction.values()) {
                String reaction = interaction.toString();
                String q = "count(CASE WHEN ia.interaction = '" + reaction + "' THEN 1 END) AS '" + reaction + "'";
                queryBuilder.SELECT.add(q);
            }

            queryBuilder.JOINS.add("LEFT JOIN Image i ON h.id = i.hoot");
            queryBuilder.JOINS.add("LEFT JOIN Post p ON h.id = p.hoot");
            queryBuilder.JOINS.add("LEFT JOIN Comment c ON h.id = c.hoot");
            queryBuilder.JOINS.add("LEFT JOIN HootTags ht ON h.id = ht.id");
            queryBuilder.JOINS.add("LEFT JOIN Tag t ON ht.tag = t.tag");
            queryBuilder.JOINS.add("LEFT JOIN Reaction r ON h.id = r.hoot");
            queryBuilder.JOINS.add("LEFT JOIN Interaction ia ON r.interaction = ia.interaction");
            queryBuilder.GROUP_BY.add("h.id");

            Connection        connection = this.getConnection();
            PreparedStatement statement  = queryBuilder.build(connection);
            ResultSet         resultSet  = statement.executeQuery();

            ArrayList<Hoot>       allHoots           = new ArrayList<>();
            UserRepository        userRepository     = (UserRepository) ObjectManager.get(UserRepository.class);
            HootTagRepository     tagRepository      = (HootTagRepository) ObjectManager.get(HootTagRepository.class);
            HootMentionRepository mentionsRepository = this.getHootMentionRepository();

            while (resultSet.next()) {
                String dbHootType = resultSet.getString("h.hootType");

                switch (HootType.valueOf(dbHootType)) {
                    // yes, this is bad, but it just works (TM)
                    case Image:
                        Image imageHoot = new Image();
                        imageHoot.id           = resultSet.getInt("h.id");
                        imageHoot.created      = this.getLocalDateTimeFromSQLTimestamp(resultSet.getTimestamp("h.created"));
                        imageHoot.imagePath    = resultSet.getString("i.imagePath");
                        imageHoot.content      = resultSet.getString("i.content");
                        imageHoot.onlyFollower = resultSet.getBoolean("i.onlyFollower");
                        imageHoot.user         = userRepository.getById(resultSet.getInt("h.user"));
                        imageHoot.mentions     = mentionsRepository.getByHootId(imageHoot.id);
                        imageHoot.hootTags = tagRepository.getByHootId(imageHoot.id);

                        for (Interaction interaction: Interaction.values()) {
                            imageHoot.reactionCount.put(interaction, resultSet.getInt(interaction.toString()));
                        }

                        allHoots.add(imageHoot);
                        break;
                    case Post:
                        Post postHoot = new Post();
                        postHoot.created      = this.getLocalDateTimeFromSQLTimestamp(resultSet.getTimestamp("h.created"));
                        postHoot.content      = resultSet.getString("i.content");
                        postHoot.onlyFollower = resultSet.getBoolean("i.onlyFollower");
                        postHoot.id           = resultSet.getInt("h.id");
                        postHoot.user         = userRepository.getById(resultSet.getInt("h.user"));
                        postHoot.mentions     = mentionsRepository.getByHootId(postHoot.id);
                        postHoot.hootTags = tagRepository.getByHootId(postHoot.id);

                        for (Interaction interaction: Interaction.values()) {
                            postHoot.reactionCount.put(interaction, resultSet.getInt(interaction.toString()));
                        }

                        allHoots.add(postHoot);
                        break;
                    case Comment:
                        Comment commentHoot  = new Comment();
                        commentHoot.created  = this.getLocalDateTimeFromSQLTimestamp(resultSet.getTimestamp("h.created"));
                        commentHoot.content  = resultSet.getString("i.content");
                        commentHoot.id       = resultSet.getInt("h.id");
                        commentHoot.user     = userRepository.getById(resultSet.getInt("h.user"));
                        commentHoot.parent   = this.getById(resultSet.getInt("c.parent"));
                        commentHoot.mentions = mentionsRepository.getByHootId(commentHoot.id);
                        commentHoot.hootTags = tagRepository.getByHootId(commentHoot.id);

                        for (Interaction interaction: Interaction.values()) {
                            commentHoot.reactionCount.put(interaction, resultSet.getInt(interaction.toString()));
                        }

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
        // delete parent hoot ID (CASCADE delete in DB)
    }

    private HootMentionRepository getHootMentionRepository()
    {
        return (HootMentionRepository) ObjectManager.get(HootMentionRepository.class);
    }
}
