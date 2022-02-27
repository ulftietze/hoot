package hoot.model.search;

import hoot.system.Database.QueryBuilder;
import hoot.system.ObjectManager.ObjectManager;

import java.sql.SQLException;

/**
 * LIST OF ALL USERS THAT ARE FOLLOWED BY THE SPECIFIED USERID
 */
public class FollowsSearchCriteria implements SearchCriteriaInterface
{
    public Integer userID;

    @Override
    public QueryBuilder getQueryBuilder() throws SQLException
    {
        if (userID == null) {
            return null;
        }

        QueryBuilder queryBuilder = (QueryBuilder) ObjectManager.create(QueryBuilder.class);

        queryBuilder.WHERE.add("user = ?");
        queryBuilder.PARAMETERS.add(this.userID.toString());

        return queryBuilder;
    }
}
