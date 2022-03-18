package hoot.model.search;

import hoot.system.Database.QueryBuilder;
import hoot.system.objects.ObjectManager;

import java.sql.SQLException;
import java.util.ArrayList;

public class UserSearchCriteria implements SearchCriteriaInterface
{
    public ArrayList<Integer> userIds = new ArrayList<>();

    public Integer createdAtSinceMinutes = null;

    @Override
    public QueryBuilder getQueryBuilder() throws SQLException
    {
        QueryBuilder queryBuilder = (QueryBuilder) ObjectManager.create(QueryBuilder.class);

        if (this.userIds.size() > 0) {
            queryBuilder.WHERE.add("userId IN (?)");
            queryBuilder.PARAMETERS.add(userIds.toString());
        }

        if (createdAtSinceMinutes != null) {
            queryBuilder.addWhere("created > (NOW() - INTERVAL ? MINUTE)", this.createdAtSinceMinutes);
        }

        return queryBuilder;
    }
}
