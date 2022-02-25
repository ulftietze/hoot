package hoot.model.search;

import hoot.system.Database.QueryBuilder;
import hoot.system.ObjectManager.ObjectManager;

import java.sql.SQLException;

public class AllHootsSearchCriteria implements SearchCriteriaInterface
{
    @Override
    public QueryBuilder getQueryBuilder() throws SQLException
    {
        return (QueryBuilder) ObjectManager.get(QueryBuilder.class, true);
    }
}
