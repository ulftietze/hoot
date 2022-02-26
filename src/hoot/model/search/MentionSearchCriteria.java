package hoot.model.search;

import hoot.model.entities.Hoot;
import hoot.system.Database.QueryBuilder;
import hoot.system.ObjectManager.ObjectManager;

import java.sql.SQLException;

public class MentionSearchCriteria implements SearchCriteriaInterface
{
    private Hoot hoot;

    public MentionSearchCriteria(Hoot hoot)
    {
        this.hoot = hoot;
    }

    @Override
    public QueryBuilder getQueryBuilder() throws SQLException
    {
        QueryBuilder queryBuilder = (QueryBuilder) ObjectManager.get(QueryBuilder.class, true);

        queryBuilder.SELECT.add("mention");

        queryBuilder.FROM = "HootMentions";

        queryBuilder.WHERE.add("hoot = ?");
        queryBuilder.PARAMETERS.add(this.hoot.id.toString());

        return queryBuilder;
    }
}
