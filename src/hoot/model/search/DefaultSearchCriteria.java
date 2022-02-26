package hoot.model.search;

import hoot.system.Database.QueryBuilder;
import hoot.system.ObjectManager.ObjectManager;

import java.sql.SQLException;

public class DefaultSearchCriteria implements SearchCriteriaInterface
{
    @Override
    public QueryBuilder getQueryBuilder() throws SQLException
    {
        return (QueryBuilder) ObjectManager.create(QueryBuilder.class);
    }
}
