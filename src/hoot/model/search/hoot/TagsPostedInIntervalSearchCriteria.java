package hoot.model.search.hoot;

import hoot.model.search.SearchCriteriaInterface;
import hoot.system.Database.QueryBuilder;
import hoot.system.ObjectManager.ObjectManager;

import java.sql.SQLException;

public class TagsPostedInIntervalSearchCriteria implements SearchCriteriaInterface
{
    public int lastModifiedInHours = 24;

    public int quantity = 10;

    @Override
    public QueryBuilder getQueryBuilder() throws SQLException
    {
        QueryBuilder queryBuilder = (QueryBuilder) ObjectManager.create(QueryBuilder.class);
        queryBuilder.addJoin("INNER JOIN Hoot h ON h.id = ht.hoot");
        queryBuilder.addWhere("h.modified > (NOW() - INTERVAL ? HOUR)", this.lastModifiedInHours);
        queryBuilder.GROUP_BY.add("ht.tag");
        queryBuilder.ORDER_BY.add("COUNT(ht.tag)");
        queryBuilder.LIMIT = quantity;

        return queryBuilder;
    }
}
