package hoot.model.repositories;

import hoot.model.entities.*;
import hoot.model.search.SearchCriteriaInterface;
import hoot.model.search.hoot.MentionSearchCriteria;
import hoot.model.search.hoot.TagSearchCriteria;
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
    public Hoot getById(Integer id) throws EntityNotFoundException
    {
        if (id == null) {
            throw new EntityNotFoundException("Hoot with ID null");
        }

        try {
            QueryBuilder queryBuilder = (QueryBuilder) ObjectManager.create(QueryBuilder.class);
            this.prepareQueryBuilder(queryBuilder);
            queryBuilder.WHERE.add("h.id = ?");
            queryBuilder.PARAMETERS.add(id.toString());

            Connection        connection = this.getConnection();
            PreparedStatement statement  = queryBuilder.build(connection);
            ResultSet         resultSet  = statement.executeQuery();

            resultSet.next();

            Hoot hoot = this.getHoot(resultSet);

            resultSet.close();
            statement.close();
            connection.close();

            return hoot;
        } catch (SQLException e) {
            this.log("Hoot.getById() + " + e.getMessage());
            throw new EntityNotFoundException("Hoot with ID " + id);
        }
    }

    @Override
    public ArrayList<Hoot> getList(SearchCriteriaInterface searchCriteria) throws EntityNotFoundException
    {
        try {
            QueryBuilder queryBuilder = searchCriteria.getQueryBuilder();

            this.prepareQueryBuilder(queryBuilder);

            Connection        connection = this.getConnection();
            PreparedStatement statement  = queryBuilder.build(connection);
            ResultSet         resultSet  = statement.executeQuery();

            ArrayList<Hoot> allHoots = new ArrayList<>();

            while (resultSet.next()) {
                allHoots.add(this.getHoot(resultSet));
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

    public HootMentions getMentions(Hoot hoot) throws EntityNotFoundException
    {
        HootMentions hootMentions = (HootMentions) ObjectManager.create(HootMentions.class);
        hootMentions.hoot = hoot;

        MentionSearchCriteria
                searchCriteria
                = (MentionSearchCriteria) ObjectManager.create(MentionSearchCriteria.class);
        searchCriteria.hoot = hoot;

        MentionRepository repository = (MentionRepository) ObjectManager.get(MentionRepository.class);
        hootMentions.mentions = repository.getList(searchCriteria);

        return hootMentions;
    }

    public HootTags getTags(Hoot hoot) throws EntityNotFoundException
    {
        HootTags hootTags = (HootTags) ObjectManager.create(HootTags.class);
        hootTags.hoot = hoot;

        TagSearchCriteria searchCriteria = (TagSearchCriteria) ObjectManager.create(TagSearchCriteria.class);
        searchCriteria.hoot = hoot;

        TagRepository repository = (TagRepository) ObjectManager.get(TagRepository.class);
        hootTags.tags = repository.getList(searchCriteria);

        return hootTags;
    }

    private void prepareQueryBuilder(QueryBuilder queryBuilder)
    {
        queryBuilder.SELECT.add("*");
        queryBuilder.FROM = "Hoot h";

        for (Interaction interaction : Interaction.values()) {
            String reaction = interaction.toString();
            String q = "count(CASE WHEN ia.interaction = '" + reaction + "' THEN 1 END) AS '" + reaction
                       + "'";
            queryBuilder.SELECT.add(q);
        }

        queryBuilder.JOINS.add("LEFT JOIN Image i ON h.id = i.hoot");
        queryBuilder.JOINS.add("LEFT JOIN Post p ON h.id = p.hoot");
        queryBuilder.JOINS.add("LEFT JOIN Comment c ON h.id = c.hoot");
        queryBuilder.JOINS.add("LEFT JOIN HootTags ht ON h.id = ht.id");
        queryBuilder.JOINS.add("LEFT JOIN Tag t ON ht.tag = t.tag");
        queryBuilder.JOINS.add("LEFT JOIN Reaction r ON h.id = r.hoot");
        queryBuilder.JOINS.add("LEFT JOIN Interaction ia ON r.interaction = ia.interaction");
        queryBuilder.JOINS.add("LEFT JOIN User u ON h.user = u.id");
        queryBuilder.GROUP_BY.add("h.id");
    }

    private Hoot getHoot(ResultSet resultSet) throws SQLException, EntityNotFoundException
    {
        UserRepository userRepository = (UserRepository) ObjectManager.get(UserRepository.class);

        String dbHootType = resultSet.getString("h.hootType");

        switch (HootType.valueOf(dbHootType)) {
            // yes, this is bad, but it just works (TM)
            case Image:
                Image imageHoot = (Image) ObjectManager.create(Image.class);
                imageHoot.id = resultSet.getInt("h.id");
                imageHoot.created = this.getLocalDateTimeFromSQLTimestamp(resultSet.getTimestamp("h.created"));
                imageHoot.imagePath = resultSet.getString("i.imagePath");
                imageHoot.content = resultSet.getString("i.content");
                imageHoot.onlyFollower = resultSet.getBoolean("i.onlyFollower");
                imageHoot.user = userRepository.getById(resultSet.getInt("h.user"));
                imageHoot.mentions = this.getMentions(imageHoot);
                imageHoot.tags = this.getTags(imageHoot);

                for (Interaction interaction : Interaction.values()) {
                    imageHoot.reactionCount.put(interaction, resultSet.getInt(interaction.toString()));
                }

                return imageHoot;
            case Post:
                Post postHoot = (Post) ObjectManager.create(Post.class);
                postHoot.created = this.getLocalDateTimeFromSQLTimestamp(resultSet.getTimestamp("h.created"));
                postHoot.content = resultSet.getString("p.content");
                postHoot.onlyFollower = resultSet.getBoolean("p.onlyFollower");
                postHoot.id = resultSet.getInt("h.id");
                postHoot.user = userRepository.getById(resultSet.getInt("h.user"));
                postHoot.mentions = this.getMentions(postHoot);
                postHoot.tags = this.getTags(postHoot);

                for (Interaction interaction : Interaction.values()) {
                    postHoot.reactionCount.put(interaction, resultSet.getInt(interaction.toString()));
                }

                return postHoot;
            case Comment:
                Comment commentHoot = (Comment) ObjectManager.create(Comment.class);
                commentHoot.created
                        = this.getLocalDateTimeFromSQLTimestamp(resultSet.getTimestamp("h.created"));
                commentHoot.content = resultSet.getString("c.content");
                commentHoot.id = resultSet.getInt("h.id");
                commentHoot.user = userRepository.getById(resultSet.getInt("h.user"));
                commentHoot.parent = this.getById(resultSet.getInt("c.parent"));
                commentHoot.mentions = this.getMentions(commentHoot);
                commentHoot.tags = this.getTags(commentHoot);

                for (Interaction interaction : Interaction.values()) {
                    commentHoot.reactionCount.put(interaction, resultSet.getInt(interaction.toString()));
                }

                return commentHoot;
            default:
                this.log("HootType from DB was not found in Java entity enum.");
                throw new EntityNotFoundException("HootType");
        }
    }
}
