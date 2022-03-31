package hoot.model.search;

import hoot.system.Database.QueryBuilder;
import hoot.system.objects.ObjectManager;

import java.sql.SQLException;

/**
 * Find a user which follows the given user
 */
public class UserFollowsUserSearchCriteria implements SearchCriteriaInterface
{
    public Integer currentUserId = null;

    public Integer userIdToCheck = null;

    @Override
    public QueryBuilder getQueryBuilder() throws SQLException
    {
        if (this.currentUserId == null || this.userIdToCheck == null) {
            throw new SQLException("Missing user ids to check.");
        }

        QueryBuilder queryBuilder = (QueryBuilder) ObjectManager.create(QueryBuilder.class);

        queryBuilder.addWhere("user = ?", this.currentUserId);
        queryBuilder.addWhere("follows = ?", this.userIdToCheck);

        return queryBuilder;
    }
}
