package hoot.model.search;

import hoot.model.entities.HootType;
import hoot.system.Database.QueryBuilder;
import hoot.system.ObjectManager.ObjectManager;

import java.sql.SQLException;

public class HootSearchCriteria implements SearchCriteriaInterface
{
    public Integer userId = null;

    public String tags = null;

    public Integer defaultPageSize = 50;

    public Integer lastPostId = null;

    public boolean withComments = false;

    @Override
    public QueryBuilder getQueryBuilder() throws SQLException
    {
        QueryBuilder qb = (QueryBuilder) ObjectManager.create(QueryBuilder.class);

        qb.LIMIT = defaultPageSize;

        if (userId != null) {
            qb.WHERE.add("userId = ?");
            qb.PARAMETERS.add(userId.toString());
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

        return qb;
    }
}
