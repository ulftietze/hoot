package hoot.model.search;

import hoot.system.Database.QueryBuilder;
import hoot.system.objects.ObjectManager;

import java.sql.SQLException;

public class HistorySearchCriteria implements SearchCriteriaInterface
{
    public Integer secondsToLoad = 3600;

    @Override
    public QueryBuilder getQueryBuilder() throws SQLException
    {
        QueryBuilder queryBuilder = (QueryBuilder) ObjectManager.create(QueryBuilder.class);
        queryBuilder.LIMIT = secondsToLoad;
        queryBuilder.ORDER_BY.add("id DESC");

        return queryBuilder;
    }
}
