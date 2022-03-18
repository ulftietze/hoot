package hoot.model.repositories;

import hoot.model.cache.HootCacheInterface;
import hoot.model.entities.*;
import hoot.model.search.SearchCriteriaInterface;
import hoot.model.search.hoot.MentionSearchCriteria;
import hoot.model.search.hoot.TagSearchCriteria;
import hoot.system.Database.QueryBuilder;
import hoot.system.Database.QueryResult;
import hoot.system.Database.QueryResultRow;
import hoot.system.Exception.CouldNotDeleteException;
import hoot.system.Exception.CouldNotSaveException;
import hoot.system.Exception.EntityNotFoundException;
import hoot.system.objects.Inject;
import hoot.system.objects.ObjectManager;

import java.sql.*;
import java.util.ArrayList;

public class HootRepository extends AbstractRepository<Hoot>
{
    @Inject private MentionRepository     mentionRepository;
    @Inject private TagRepository         tagRepository;
    @Inject private UserRepository        userRepository;
    @Inject private HootTagRepository     hootTagRepository;
    @Inject private HootMentionRepository hootMentionRepository;
    @Inject private HootCacheInterface    hootCache;

    public Hoot getById(Integer id) throws EntityNotFoundException
    {
        if (id == null) {
            throw new EntityNotFoundException("Hoot with ID null");
        }

        Hoot hoot = this.hootCache.get(id);

        if (hoot != null) {
            return hoot;
        }

        try (Connection connection = this.getConnection()) {
            QueryBuilder queryBuilder = (QueryBuilder) ObjectManager.create(QueryBuilder.class);
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
            queryBuilder.WHERE.add("h.id = ?");
            queryBuilder.PARAMETERS.add(id.toString());

            PreparedStatement statement = queryBuilder.build(connection);
            QueryResultRow    resultRow = this.statementFetcher.fetchOne(statement);
            connection.close();

            hoot = this.mapHoot(resultRow);
        } catch (SQLException e) {
            this.logger.logException("Hoot by Id: " + id + " ==>" + e.getMessage(), e);
            throw new EntityNotFoundException("Hoot with ID " + id);
        }

        this.hootCache.put(hoot);

        return hoot;
    }

    @Override
    public ArrayList<Hoot> getList(SearchCriteriaInterface searchCriteria) throws EntityNotFoundException
    {
        ArrayList<Hoot> allHoots = new ArrayList<>();

        try (Connection connection = this.getConnection()) {
            QueryBuilder queryBuilder = searchCriteria.getQueryBuilder();
            queryBuilder.SELECT.add("h.id");
            queryBuilder.FROM = "Hoot h";
            queryBuilder.JOINS.add("LEFT JOIN Image i ON h.id = i.hoot");
            queryBuilder.JOINS.add("LEFT JOIN Post p ON h.id = p.hoot");
            queryBuilder.JOINS.add("LEFT JOIN Comment c ON h.id = c.hoot");
            queryBuilder.JOINS.add("LEFT JOIN HootTags ht ON h.id = ht.hoot");
            queryBuilder.JOINS.add("LEFT JOIN Tag t ON ht.tag = t.tag");
            queryBuilder.JOINS.add("LEFT JOIN User u ON h.user = u.id");

            PreparedStatement statement = queryBuilder.build(connection);

            QueryResult queryResult = this.statementFetcher.fetchAll(statement);
            connection.close();

            for (QueryResultRow row : queryResult) {
                Integer hootId = (Integer) row.get("Hoot.id");
                allHoots.add(this.getById(hootId));
            }
        } catch (SQLException e) {
            this.log("Could not load list of Hoots: " + e.getMessage());
        }

        return allHoots;
    }

    private void create(Hoot hoot) throws CouldNotSaveException
    {
        try (Connection connection = this.getConnection()) {
            String hootStatement = "insert into Hoot (user, hootType) values (?, ?)";

            var hootPss = connection.prepareStatement(hootStatement, PreparedStatement.RETURN_GENERATED_KEYS);
            hootPss.setInt(1, hoot.user.id);
            hootPss.setString(2, hoot.hootType.toString());

            int       rowCount = hootPss.executeUpdate();
            ResultSet rs       = hootPss.getGeneratedKeys();
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
                    String postStatement = "INSERT INTO Post (hoot, content, onlyFollower) VALUES (?, ?, ?)";

                    PreparedStatement postPss = connection.prepareStatement(postStatement);

                    postPss.setInt(1, post.id);
                    postPss.setString(2, post.content);
                    postPss.setBoolean(3, post.onlyFollower);

                    int postRows = this.statementFetcher.executeUpdate(postPss);
                    connection.close();

                    if (postRows == 0) {
                        throw new CouldNotSaveException("Post with ID " + post.id);
                    }

                    break;
                case Image:
                    Image image = (Image) hoot;
                    String query = "insert into Image (hoot, imagePath, content, onlyFollower) values (?, ?, ?, ?)";

                    PreparedStatement imagePss = connection.prepareStatement(query);

                    imagePss.setInt(1, image.id);
                    imagePss.setString(2, image.imagePath);
                    imagePss.setString(3, image.content);
                    imagePss.setBoolean(4, image.onlyFollower);

                    int imageRows = this.statementFetcher.executeUpdate(imagePss);
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

                    int commentRows = this.statementFetcher.executeUpdate(commentPss);
                    connection.close();

                    if (commentRows == 0) {
                        throw new CouldNotSaveException("Comment with ID " + comment.id);
                    }

                    break;
            }

            this.hootMentionRepository.save(hoot.mentions);
            this.hootTagRepository.save(hoot.tags);
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

                    int postRows = this.statementFetcher.executeUpdate(postPss);
                    connection.close();

                    if (postRows == 0) {
                        throw new CouldNotSaveException("Post with ID " + post.id);
                    }

                    break;
                case Image:
                    Image image = (Image) hoot;
                    String imageStatement = "UPDATE Image " + "SET imagePath = ?, content = ?, onlyFollower = ? "
                                            + "WHERE hoot = ?";

                    PreparedStatement imagePss = connection.prepareStatement(imageStatement);
                    imagePss.setString(1, image.imagePath);
                    imagePss.setString(2, image.content);
                    imagePss.setBoolean(3, image.onlyFollower);
                    imagePss.setInt(4, image.id);

                    int imageRows = this.statementFetcher.executeUpdate(imagePss);
                    connection.close();

                    if (imageRows == 0) {
                        throw new CouldNotSaveException("Image with ID " + image.id);
                    }

                    break;
                case Comment:
                    Comment comment = (Comment) hoot;
                    String commentStatement = "UPDATE Comment SET content = ? WHERE hoot = ?";

                    PreparedStatement commentPss = connection.prepareStatement(commentStatement);
                    commentPss.setString(1, comment.content);
                    commentPss.setInt(2, comment.id);

                    int commentRows = this.statementFetcher.executeUpdate(commentPss);
                    connection.close();

                    if (commentRows == 0) {
                        throw new CouldNotSaveException("Comment with ID " + comment.id);
                    }

                    break;
            }

            this.hootMentionRepository.save(hoot.mentions);
            this.hootTagRepository.save(hoot.tags);
        } catch (SQLException e) {
            this.log("Hoot.save(): " + e.getMessage());
            throw new CouldNotSaveException("Hoot");
        }

        this.hootCache.purge(hoot);
    }

    @Override
    public void delete(Hoot hoot) throws CouldNotDeleteException
    {
        if (hoot.id == null) {
            throw new CouldNotDeleteException("Hoot with ID null");
        }

        try (Connection connection = this.getConnection()) {
            String sqlStatement = "delete from Hoot where id = ?";

            PreparedStatement statement = connection.prepareStatement(sqlStatement);
            statement.setInt(1, hoot.id);

            int rowCount = this.statementFetcher.executeUpdate(statement);
            connection.close();

            if (rowCount == 0) {
                throw new CouldNotDeleteException("Hoot with ID " + hoot.id);
            }

            if (hoot.hootType == HootType.Image) {
                Image image = (Image) hoot;
                this.mediaFileHandler.deleteMedia(image.imagePath);
            }
        } catch (SQLException e) {
            this.log("Hoot.delete(): " + e.getMessage());
            throw new CouldNotDeleteException("Hoot with ID " + hoot.id);
        }

        this.hootCache.purge(hoot);
    }

    public HootMentions getMentions(Hoot hoot) throws EntityNotFoundException
    {
        var searchCriteria = (MentionSearchCriteria) ObjectManager.create(MentionSearchCriteria.class);
        searchCriteria.hoot = hoot;

        var hootMentions = (HootMentions) ObjectManager.create(HootMentions.class);
        hootMentions.hoot     = hoot;
        hootMentions.mentions = this.mentionRepository.getList(searchCriteria);

        return hootMentions;
    }

    public HootTags getTags(Hoot hoot) throws EntityNotFoundException
    {
        TagSearchCriteria searchCriteria = (TagSearchCriteria) ObjectManager.create(TagSearchCriteria.class);
        searchCriteria.hoot = hoot;

        HootTags hootTags = (HootTags) ObjectManager.create(HootTags.class);
        hootTags.hoot = hoot;
        hootTags.tags = this.tagRepository.getList(searchCriteria);

        return hootTags;
    }

    private Hoot mapHoot(QueryResultRow resultRow) throws SQLException
    {
        String dbHootType = (String) resultRow.get("Hoot.hootType");

        switch (HootType.valueOf(dbHootType)) {
            // yes, this is not optimal, but it just works (TM)
            case Image:
                Image imageHoot = (Image) ObjectManager.create(Image.class);
                imageHoot.id = (Integer) resultRow.get("Hoot.id");
                imageHoot.created = this.getLocalDateTimeFromSQLTimestamp((Timestamp) resultRow.get("Hoot.created"));
                imageHoot.imagePath = (String) resultRow.get("Image.imagePath");
                imageHoot.content = (String) resultRow.get("Image.content");
                imageHoot.onlyFollower = (boolean) resultRow.get("Image.onlyFollower");
                imageHoot.user = this.userRepository.getById((int) resultRow.get("Hoot.user"));
                imageHoot.mentions = this.getMentions(imageHoot);
                imageHoot.tags = this.getTags(imageHoot);

                for (Interaction interaction : Interaction.values()) {
                    imageHoot.reactionCount.put(interaction, (Long) resultRow.get(interaction.toString()));
                }

                return imageHoot;
            case Post:
                Post postHoot = (Post) ObjectManager.create(Post.class);
                postHoot.created = this.getLocalDateTimeFromSQLTimestamp((Timestamp) resultRow.get("Hoot.created"));
                postHoot.content = (String) resultRow.get("Post.content");
                postHoot.onlyFollower = (boolean) resultRow.getOrDefault("Post.onlyFollower", false);
                postHoot.id = (int) resultRow.get("Hoot.id");
                postHoot.user = this.userRepository.getById((int) resultRow.get("Hoot.user"));
                postHoot.mentions = this.getMentions(postHoot);
                postHoot.tags = this.getTags(postHoot);

                for (Interaction interaction : Interaction.values()) {
                    postHoot.reactionCount.put(interaction, (Long) resultRow.get(interaction.toString()));
                }

                return postHoot;
            case Comment:
                Comment commentHoot = (Comment) ObjectManager.create(Comment.class);
                commentHoot.created = this.getLocalDateTimeFromSQLTimestamp((Timestamp) resultRow.get("Hoot.created"));
                commentHoot.content = (String) resultRow.get("Comment.content");
                commentHoot.id = (int) resultRow.get("Hoot.id");
                commentHoot.user = this.userRepository.getById((int) resultRow.get("Hoot.user"));
                commentHoot.parent = this.getById((int) resultRow.get("Comment.parent"));
                commentHoot.mentions = this.getMentions(commentHoot);
                commentHoot.tags = this.getTags(commentHoot);

                for (Interaction interaction : Interaction.values()) {
                    commentHoot.reactionCount.put(interaction, (Long) resultRow.get(interaction.toString()));
                }

                return commentHoot;
            default:
                this.log("HootType from DB was not found in Java entity enum.");
                throw new EntityNotFoundException("HootType");
        }
    }
}
