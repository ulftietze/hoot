package hoot.model.search.hoot;

import hoot.model.entities.Follower;
import hoot.model.entities.HootType;
import hoot.model.repositories.FollowerRepository;
import hoot.model.search.FollowsSearchCriteria;
import hoot.model.search.SearchCriteriaInterface;
import hoot.system.Database.QueryBuilder;
import hoot.system.Exception.EntityNotFoundException;
import hoot.system.ObjectManager.ObjectManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class TimelineSearchCriteria implements SearchCriteriaInterface
{
    private final FollowerRepository followerRepository;
    public        Integer            userId          = null;
    public        String             tags            = null;
    public        Integer            defaultPageSize = 50;
    public        Integer            lastPostId      = null;
    public        boolean            withComments    = false;

    public TimelineSearchCriteria()
    {
        this.followerRepository = (FollowerRepository) ObjectManager.get(FollowerRepository.class);
    }

    @Override
    public QueryBuilder getQueryBuilder() throws SQLException
    {
        QueryBuilder qb = (QueryBuilder) ObjectManager.create(QueryBuilder.class);

        int[] followsIds = this
                .getFollowerForUserId(this.userId)
                .stream()
                .mapToInt(follower -> follower.follows.id)
                .toArray();

        String params = Arrays.stream(followsIds).mapToObj(id -> "?").collect(Collectors.joining(", "));

        qb.WHERE.add("h.user in (" + params + ")");
        for (int id : followsIds) {
            qb.PARAMETERS.add(id);
        }

        if (tags != null && !tags.equals("")) {
            qb.WHERE.add("t.tag IN (?) ");
            qb.PARAMETERS.add(tags);
        }

        if (lastPostId != null) {
            // IDs are incremental, so this is easier than a timestamp comparison
            qb.WHERE.add("h.id < ?");
            qb.PARAMETERS.add(lastPostId.toString());
        }

        if (!withComments) {
            qb.WHERE.add("h.hootType != ?");
            qb.PARAMETERS.add(HootType.Comment.toString());
        }

        qb.LIMIT = this.defaultPageSize;

        return qb;
    }

    private ArrayList<Follower> getFollowerForUserId(Integer userId) throws EntityNotFoundException
    {
        FollowsSearchCriteria followsSearchCriteria = this.getFollowsSearchCriteria();
        followsSearchCriteria.userID = userId;

        return this.followerRepository.getList(followsSearchCriteria);
    }

    private FollowsSearchCriteria getFollowsSearchCriteria()
    {
        return (FollowsSearchCriteria) ObjectManager.create(FollowsSearchCriteria.class);
    }
}
