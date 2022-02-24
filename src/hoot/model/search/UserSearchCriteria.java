package hoot.model.search;

import hoot.system.Logger.LoggerInterface;
import hoot.system.ObjectManager.ObjectManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class UserSearchCriteria implements SearchCriteriaInterface
{
    public Integer userId = null;

    public Integer defaultPageSize = 50;

    @Override
    public PreparedStatement getQueryStatement(Connection connection) throws SQLException
    {
        ArrayList<String> WHERE      = new ArrayList<>();
        ArrayList<String> PARAMETERS = new ArrayList<>();

        String SELECT = "SELECT * FROM User u ";
        String LIMIT  = "LIMIT " + defaultPageSize;

        if (userId != null) {
            WHERE.add("userId = ?");
            PARAMETERS.add(userId.toString());
        }

        StringBuilder QUERY = new StringBuilder(SELECT);

        if (WHERE.size() >= 1) {
            QUERY.append(" WHERE ").append(WHERE.get(0));

            for (int i = 1; i < WHERE.size(); i++) {
                QUERY.append(" AND ").append(WHERE.get(i));
            }
        }

        QUERY.append(" ").append(LIMIT);

        LoggerInterface logger = (LoggerInterface) ObjectManager.get(LoggerInterface.class);
        logger.log(QUERY.toString());

        PreparedStatement statement = connection.prepareStatement(QUERY.toString());

        for (int i = 1; i <= PARAMETERS.size(); i++) {
            statement.setString(i, PARAMETERS.get(i - 1));
        }

        return statement;
    }
}
