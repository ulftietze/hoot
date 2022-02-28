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

public class TimelineSearchCriteria implements SearchCriteriaInterface
{
    private final FollowerRepository followerRepository;
    public        Integer            timelineForUserId = null;
    public        ArrayList<String>  tags              = new ArrayList<>();
    public        Integer            defaultPageSize   = 50;
    public        Integer            lastPostId        = null;
    public        boolean            withComments      = false;

    public TimelineSearchCriteria()
    {
        this.followerRepository = (FollowerRepository) ObjectManager.get(FollowerRepository.class);
    }

    @Override
    public QueryBuilder getQueryBuilder() throws SQLException
    {
        QueryBuilder qb = (QueryBuilder) ObjectManager.create(QueryBuilder.class);

        ArrayList<Integer> followIds = new ArrayList<>();
        this.getFollowerForUserId(this.timelineForUserId).forEach(follower -> followIds.add(follower.follows.id));

        qb.addWhereIn("h.user", followIds);
        qb.addWhereIn("t.tag", tags);

        if (this.lastPostId != null) {
            // IDs are incremental, so this is easier+quicker than a timestamp comparison
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
        followsSearchCriteria.userId = userId;

        return this.followerRepository.getList(followsSearchCriteria);
    }

    private FollowsSearchCriteria getFollowsSearchCriteria()
    {
        return (FollowsSearchCriteria) ObjectManager.create(FollowsSearchCriteria.class);
    }
}
