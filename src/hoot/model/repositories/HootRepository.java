package hoot.model.repositories;

import hoot.model.entities.*;
import hoot.model.queue.publisher.TagsPublisher;
import hoot.model.search.SearchCriteriaInterface;
import hoot.model.search.hoot.MentionSearchCriteria;
import hoot.model.search.hoot.TagSearchCriteria;
import hoot.system.Database.QueryBuilder;
import hoot.system.Exception.CouldNotDeleteException;
import hoot.system.Exception.CouldNotSaveException;
import hoot.system.Exception.EntityNotFoundException;
import hoot.system.Filesystem.MediaFileHandler;
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

        Hoot hoot;

        try (Connection connection = this.getConnection()) {
            QueryBuilder queryBuilder = (QueryBuilder) ObjectManager.create(QueryBuilder.class);
            this.prepareQueryBuilder(queryBuilder);
            queryBuilder.WHERE.add("h.id = ?");
            queryBuilder.PARAMETERS.add(id.toString());

            PreparedStatement statement  = queryBuilder.build(connection);
            ResultSet         resultSet  = statement.executeQuery();

            resultSet.next();

            hoot = this.getHoot(resultSet);

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            this.log("Hoot.getById() + " + e.getMessage());
            throw new EntityNotFoundException("Hoot with ID " + id);
        }

        return hoot;
    }

    @Override
    public ArrayList<Hoot> getList(SearchCriteriaInterface searchCriteria) throws EntityNotFoundException
    {
        ArrayList<Hoot> allHoots = new ArrayList<>();

        try (Connection connection = this.getConnection()) {
            QueryBuilder queryBuilder = searchCriteria.getQueryBuilder();

            this.prepareQueryBuilder(queryBuilder);

            PreparedStatement statement  = queryBuilder.build(connection);
            ResultSet         resultSet  = statement.executeQuery();

            while (resultSet.next()) {
                allHoots.add(this.getHoot(resultSet));
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            this.log("Could not load list of Hoots: " + e.getMessage());
        }

        return allHoots;
    }

    private void create(Hoot hoot) throws CouldNotSaveException
    {
        try (Connection connection = this.getConnection()) {
            String hootStatement = "insert into Hoot (user, hootType) values (?, ?)";
            PreparedStatement hootPss = connection.prepareStatement(
                    hootStatement,
                    PreparedStatement.RETURN_GENERATED_KEYS
            );
            hootPss.setInt(1, hoot.user.id);
            hootPss.setString(2, hoot.hootType.toString());

            int rowCount = hootPss.executeUpdate();

            ResultSet rs = hootPss.getGeneratedKeys();
            rs.next();

            hoot.id = rs.getInt(1);

            rs.close();
            hootPss.close();

            if (rowCount == 0 || hoot.id == null) {
                throw new CouldNotSaveException("new Hoot");
            }

            switch (hoot.hootType) {
                case Post:
                    Post post = (Post) hoot;

                    String postStatement = "insert into Post (hoot, content, onlyFollower) values (?, ?, ?)";
                    PreparedStatement postPss = connection.prepareStatement(postStatement);

                    postPss.setInt(1, post.id);
                    postPss.setString(2, post.content);
                    postPss.setBoolean(3, post.onlyFollower);

                    int postRows = postPss.executeUpdate();

                    postPss.close();
                    connection.close();

                    if (postRows == 0) {
                        throw new CouldNotSaveException("Post with ID " + post.id);
                    }

                    break;
                case Image:
                    Image image = (Image) hoot;

                    String imageStatement = "insert into Image (hoot, imagePath, content, onlyFollower) values (?, ?, ?, ?)";
                    PreparedStatement imagePss = connection.prepareStatement(imageStatement);

                    imagePss.setInt(1, image.id);
                    imagePss.setString(2, image.imagePath);
                    imagePss.setString(3, image.content);
                    imagePss.setBoolean(4, image.onlyFollower);

                    int imageRows = imagePss.executeUpdate();

                    imagePss.close();
                    connection.close();

                    if (imageRows == 0) {
                        throw new CouldNotSaveException("Image with ID " + image.id);
                    }

                    break;
                case Comment:
                    Comment comment = (Comment) hoot;

                    String commentStatement = "insert into Comment (hoot, parent, content) values (?, ?, ?)";
                    PreparedStatement commentPss = connection.prepareStatement(commentStatement);

                    commentPss.setInt(1, comment.id);
                    commentPss.setInt(2, comment.parent.id);
                    commentPss.setString(3, comment.content);

                    int commentRows = commentPss.executeUpdate();

                    commentPss.close();
                    connection.close();

                    if (commentRows == 0) {
                        throw new CouldNotSaveException("Comment with ID " + comment.id);
                    }

                    break;
            }

            this.getHootMentionRepository().save(hoot.mentions);
            this.getHootTagRepository().save(hoot.tags);

            TagsPublisher tagsPublisher = (TagsPublisher) ObjectManager.get(TagsPublisher.class);
            tagsPublisher.publish(hoot.tags);
        } catch (SQLException e) {
            this.log("Hoot.save(): " + e.getMessage());
            throw new CouldNotSaveException("Hoot");
        }
    }

    /**
     * Save or update a Hoot in the DB.<br>
     * If Hoot.id is null, a new Hoot is inserted. If Hoot.id is set, the method will try to update the existing Hoot.
     *
     * @param hoot a Hoot object (Post, Image, Comment)
     * @throws CouldNotSaveException if the Hoot could not be saved
     */
    @Override
    public void save(Hoot hoot) throws CouldNotSaveException
    {
        if (hoot.id == null) {
            if (hoot.user != null && hoot.user.id != null) {
                this.create(hoot);
                return;
            } else {
                throw new CouldNotSaveException("Hoot with empty ID and empty User");
            }
        }

        try (Connection connection = this.getConnection()) {
            switch (hoot.hootType) {
                case Post:
                    Post post = (Post) hoot;

                    String postStatement = "update Post set content = ?, onlyFollower = ? where hoot = ?";
                    PreparedStatement postPss = connection.prepareStatement(postStatement);

                    postPss.setString(1, post.content);
                    postPss.setBoolean(2, post.onlyFollower);
                    postPss.setInt(3, post.id);

                    int postRows = postPss.executeUpdate();

                    postPss.close();
                    connection.close();

                    if (postRows == 0) {
                        throw new CouldNotSaveException("Post with ID " + post.id);
                    }

                    break;
                case Image:
                    Image image = (Image) hoot;

                    String imageStatement = "update Image set imagePath = ?, content = ?, onlyFollower = ? where hoot = ?";
                    PreparedStatement imagePss = connection.prepareStatement(imageStatement);

                    imagePss.setString(1, image.imagePath);
                    imagePss.setString(2, image.content);
                    imagePss.setBoolean(3, image.onlyFollower);
                    imagePss.setInt(4, image.id);

                    int imageRows = imagePss.executeUpdate();

                    imagePss.close();
                    connection.close();

                    if (imageRows == 0) {
                        throw new CouldNotSaveException("Image with ID " + image.id);
                    }

                    break;
                case Comment:
                    Comment comment = (Comment) hoot;

                    String commentStatement = "update Comment set content = ? where hoot = ?";
                    PreparedStatement commentPss = connection.prepareStatement(commentStatement);

                    commentPss.setString(1, comment.content);
                    commentPss.setInt(2, comment.id);

                    int commentRows = commentPss.executeUpdate();

                    commentPss.close();
                    connection.close();

                    if (commentRows == 0) {
                        throw new CouldNotSaveException("Comment with ID " + comment.id);
                    }

                    break;
            }

            this.getHootMentionRepository().save(hoot.mentions);
            this.getHootTagRepository().save(hoot.tags);

            TagsPublisher tagsPublisher = (TagsPublisher) ObjectManager.get(TagsPublisher.class);
            tagsPublisher.publish(hoot.tags);
        } catch (SQLException e) {
            this.log("Hoot.save(): " + e.getMessage());
            throw new CouldNotSaveException("Hoot");
        }
    }

    @Override
    public void delete(Hoot hoot) throws CouldNotDeleteException
    {
        if (hoot.id == null) {
            throw new CouldNotDeleteException("Hoot with ID null");
        }

        try (Connection connection = this.getConnection()) {
            String            sqlStatement = "delete from Hoot where id = ?";
            PreparedStatement pss          = connection.prepareStatement(sqlStatement);
            pss.setInt(1, hoot.id);
            int rowCount = pss.executeUpdate();

            if (rowCount == 0) {
                throw new CouldNotDeleteException("Hoot with ID " + hoot.id);
            }

            pss.close();

            if (hoot.hootType == HootType.Image) {
                Image            image            = (Image) hoot;
                MediaFileHandler mediaFileHandler = (MediaFileHandler) ObjectManager.get(MediaFileHandler.class);
                mediaFileHandler.deleteMedia(image.imagePath);
            }
        } catch (SQLException e) {
            this.log("Hoot.delete(): " + e.getMessage());
            throw new CouldNotDeleteException("Hoot with ID " + hoot.id);
        }
    }

    public HootMentions getMentions(Hoot hoot) throws EntityNotFoundException
    {
        var hootMentions   = (HootMentions) ObjectManager.create(HootMentions.class);
        hootMentions.hoot = hoot;
        var searchCriteria = (MentionSearchCriteria) ObjectManager.create(MentionSearchCriteria.class);
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
        queryBuilder.JOINS.add("LEFT JOIN HootTags ht ON h.id = ht.hoot");
        queryBuilder.JOINS.add("LEFT JOIN Tag t ON ht.tag = t.tag");
        queryBuilder.JOINS.add("LEFT JOIN Reaction r ON h.id = r.hoot");
        queryBuilder.JOINS.add("LEFT JOIN Interaction ia ON r.interaction = ia.interaction");
        queryBuilder.JOINS.add("LEFT JOIN User u ON h.user = u.id");
        queryBuilder.GROUP_BY.add("h.id");
    }

    private Hoot getHoot(ResultSet resultSet) throws SQLException
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

    private HootMentionRepository getHootMentionRepository()
    {
        return (HootMentionRepository) ObjectManager.get(HootMentionRepository.class);
    }

    private HootTagRepository getHootTagRepository()
    {
        return (HootTagRepository) ObjectManager.get(HootTagRepository.class);
    }
}
