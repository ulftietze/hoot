package hoot.model.search;

import hoot.system.Database.QueryBuilder;
import hoot.system.ObjectManager.ObjectManager;

import java.sql.SQLException;

/**
 * LIST OF ALL USERS THAT FOLLOW THE SPECIFIED USERID
 */
public class FollowingSearchCriteria implements SearchCriteriaInterface
{
    public Integer userId;
    public Integer lastUserId;
    public Integer defaultPageSize = 50;

    @Override
    public QueryBuilder getQueryBuilder() throws SQLException
    {
        if (userId == null) {
            throw new SQLException("Required Parameter UserId is missing");
        }

        QueryBuilder queryBuilder = (QueryBuilder) ObjectManager.create(QueryBuilder.class);

        queryBuilder.addWhere("follows = ?", this.userId);

        if (this.lastUserId != null) {
            String where = "created < (SELECT created FROM Follower WHERE user = ? AND follows = ?)";
            queryBuilder.addWhere(where, this.lastUserId, this.userId);
        }

        queryBuilder.LIMIT = defaultPageSize;

        return queryBuilder;
    }
}
