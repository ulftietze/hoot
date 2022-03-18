package hoot.model.search.hoot;

import hoot.model.entities.Hoot;
import hoot.model.search.SearchCriteriaInterface;
import hoot.system.Database.QueryBuilder;
import hoot.system.objects.ObjectManager;

import java.sql.SQLException;

public class MentionSearchCriteria implements SearchCriteriaInterface
{
    public Hoot hoot;

    @Override
    public QueryBuilder getQueryBuilder() throws SQLException
    {
        if (hoot == null) return null;

        QueryBuilder queryBuilder = (QueryBuilder) ObjectManager.create(QueryBuilder.class);

        queryBuilder.WHERE.add("hoot = ?");
        queryBuilder.PARAMETERS.add(this.hoot.id.toString());

        return queryBuilder;
    }
}
