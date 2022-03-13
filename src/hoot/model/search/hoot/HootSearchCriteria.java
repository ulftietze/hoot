package hoot.model.search.hoot;

import hoot.model.entities.HootType;
import hoot.model.search.SearchCriteriaInterface;
import hoot.system.Database.QueryBuilder;
import hoot.system.ObjectManager.ObjectManager;

import java.sql.SQLException;
import java.util.ArrayList;

public class HootSearchCriteria implements SearchCriteriaInterface
{
    public Integer           userId          = null;
    public ArrayList<String> tags            = new ArrayList<>();
    public Integer           defaultPageSize = 50;
    public Integer           lastPostId      = null;
    public boolean           withComments    = false;

    @Override
    public QueryBuilder getQueryBuilder() throws SQLException
    {
        QueryBuilder queryBuilder = (QueryBuilder) ObjectManager.create(QueryBuilder.class);

        queryBuilder.LIMIT = defaultPageSize;

        if (userId != null) {
            queryBuilder.WHERE.add("h.user = ?");
            queryBuilder.PARAMETERS.add(userId.toString());
        }

        queryBuilder.addWhereIn("t.tag", tags);

        if (lastPostId != null) {
            // IDs are incremental, so this is easier+quicker than a timestamp comparison
            queryBuilder.WHERE.add("h.id < ?");
            queryBuilder.PARAMETERS.add(lastPostId.toString());
        }

        if (!withComments) {
            queryBuilder.WHERE.add("h.hootType != ?");
            queryBuilder.PARAMETERS.add(HootType.Comment.toString());
        }

        queryBuilder.ORDER_BY.add("h.id DESC");

        return queryBuilder;
    }
}
