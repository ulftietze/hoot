package hoot.model.search.hoot;

import hoot.model.entities.Follower;
import hoot.model.entities.HootType;
import hoot.model.repositories.FollowerRepository;
import hoot.model.search.FollowsSearchCriteria;
import hoot.model.search.SearchCriteriaInterface;
import hoot.system.Database.QueryBuilder;
import hoot.system.Exception.EntityNotFoundException;
import hoot.system.objects.ObjectManager;

import java.sql.SQLException;
import java.util.ArrayList;

public class TimelineMineSearchCriteria implements SearchCriteriaInterface
{
    private final FollowerRepository followerRepository;
    public        Integer            timelineForUserId = null;
    public        ArrayList<String>  tags              = new ArrayList<>();
    public        Integer            defaultPageSize   = 50;
    public        Integer            lastPostId        = null;
    public        boolean            withComments      = false;

    public TimelineMineSearchCriteria()
    {
        this.followerRepository = (FollowerRepository) ObjectManager.get(FollowerRepository.class);
    }

    @Override
    public QueryBuilder getQueryBuilder() throws SQLException
    {
        QueryBuilder queryBuilder = (QueryBuilder) ObjectManager.create(QueryBuilder.class);

        ArrayList<Integer> followIds = new ArrayList<>();
        followIds.add(timelineForUserId);
        this.getFollowerForUserId(this.timelineForUserId).forEach(follower -> followIds.add(follower.follows.id));

        queryBuilder.addWhereIn("h.user", followIds);
        queryBuilder.addWhereIn("t.tag", tags);

        if (this.lastPostId != null) {
            // IDs are incremental, so this is easier+quicker than a timestamp comparison
            queryBuilder.WHERE.add("h.id < ?");
            queryBuilder.PARAMETERS.add(lastPostId.toString());
        }

        if (!withComments) {
            queryBuilder.WHERE.add("h.hootType != ?");
            queryBuilder.PARAMETERS.add(HootType.Comment.toString());
        }

        queryBuilder.ORDER_BY.add("h.id DESC");
        queryBuilder.LIMIT = this.defaultPageSize;

        return queryBuilder;
    }

    private ArrayList<Follower> getFollowerForUserId(Integer userId) throws EntityNotFoundException
    {
        FollowsSearchCriteria followsSearchCriteria = this.getFollowsSearchCriteria();
        followsSearchCriteria.userId = userId;

        return this.followerRepository.getList(followsSearchCriteria);
    }

    private FollowsSearchCriteria getFollowsSearchCriteria()
    {
        return (FollowsSearchCriteria) ObjectManager.create(FollowsSearchCriteria.class);
    }
}
