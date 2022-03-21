package hoot.model.search.hoot;

import hoot.model.entities.HootType;
import hoot.model.repositories.FollowerRepository;
import hoot.model.search.SearchCriteriaInterface;
import hoot.system.Database.QueryBuilder;
import hoot.system.objects.ObjectManager;

import java.sql.SQLException;
import java.util.ArrayList;

public class TimelineGlobalSearchCriteria implements SearchCriteriaInterface
{
    private final FollowerRepository followerRepository;
    public        ArrayList<String>  tags            = new ArrayList<>();
    public        Integer            defaultPageSize = 50;
    public        Integer            lastPostId      = null;
    public        boolean            withComments    = false;

    public TimelineGlobalSearchCriteria()
    {
        this.followerRepository = (FollowerRepository) ObjectManager.get(FollowerRepository.class);
    }

    @Override
    public QueryBuilder getQueryBuilder() throws SQLException
    {
        QueryBuilder queryBuilder = (QueryBuilder) ObjectManager.create(QueryBuilder.class);

        // TODO: ONLY FOLLOWERS AS SEARCH CRITERIA (WHERE CONDITION) IS MISSING

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
}
