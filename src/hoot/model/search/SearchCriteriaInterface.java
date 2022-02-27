package hoot.model.search;

import hoot.system.Database.QueryBuilder;

import java.sql.SQLException;

public interface SearchCriteriaInterface
{
    public QueryBuilder getQueryBuilder() throws SQLException;
}
