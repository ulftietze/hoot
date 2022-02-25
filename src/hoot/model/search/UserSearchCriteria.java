package hoot.model.search;

import hoot.system.Database.QueryBuilder;
import hoot.system.ObjectManager.ObjectManager;

import java.sql.SQLException;
import java.util.ArrayList;

public class UserSearchCriteria implements SearchCriteriaInterface
{
    public ArrayList<Integer> userIds = new ArrayList<>();

    @Override
    public QueryBuilder getQueryBuilder() throws SQLException
    {
        QueryBuilder qb = (QueryBuilder) ObjectManager.get(QueryBuilder.class, true);

        if (this.userIds.size() > 0) {
            qb.WHERE.add("userId IN (?)");
            qb.PARAMETERS.add(userIds.toString());
        }

        return qb;
    }
}
