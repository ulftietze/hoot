package hoot.model.search;

import hoot.model.entities.Hoot;
import hoot.system.Database.QueryBuilder;
import hoot.system.ObjectManager.ObjectManager;

import java.sql.SQLException;

public class MentionSearchCriteria implements SearchCriteriaInterface
{
    public Hoot hoot;

    @Override
    public QueryBuilder getQueryBuilder() throws SQLException
    {
        if (hoot == null) return null;

        QueryBuilder queryBuilder = (QueryBuilder) ObjectManager.create(QueryBuilder.class);

        queryBuilder.SELECT.add("mention");

        queryBuilder.FROM = "HootMentions";

        queryBuilder.WHERE.add("hoot = ?");
        queryBuilder.PARAMETERS.add(this.hoot.id.toString());

        return queryBuilder;
    }
}
