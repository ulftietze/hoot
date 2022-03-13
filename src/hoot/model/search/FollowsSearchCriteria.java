package hoot.model.search;

import hoot.system.Database.QueryBuilder;
import hoot.system.ObjectManager.ObjectManager;

import java.sql.SQLException;

/**
 * LIST OF ALL USERS THAT ARE FOLLOWED BY THE SPECIFIED USERID
 */
public class FollowsSearchCriteria implements SearchCriteriaInterface
{
    public Integer userId = null;

    public Integer lastUserId = null;

    public Integer defaultPageSize = 50;

    @Override
    public QueryBuilder getQueryBuilder() throws SQLException
    {
        if (userId == null) {
            throw new SQLException("Required Parameter UserId is missing");
        }

        QueryBuilder queryBuilder = (QueryBuilder) ObjectManager.create(QueryBuilder.class);

        queryBuilder.addWhere("user = ?", this.userId);

        if (this.lastUserId != null) {
            String where = "created < (SELECT created FROM Follower WHERE user = ? AND follows = ?)";
            queryBuilder.addWhere(where, this.userId, this.lastUserId);
        }

        queryBuilder.LIMIT = defaultPageSize;

        return queryBuilder;
    }
}
