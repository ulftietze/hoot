package hoot.model.search;

import hoot.model.entities.HootType;
import hoot.system.Logger.LoggerInterface;
import hoot.system.ObjectManager.ObjectManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class HootSearchCriteria implements SearchCriteriaInterface
{
    public Integer userId = null;

    public String tags = null;

    public Integer defaultPageSize = 50;

    public Integer lastPostId = null;

    public boolean withComments = false;

    @Override
    public PreparedStatement getQueryStatement(Connection connection) throws SQLException
    {
        ArrayList<String> WHERE      = new ArrayList<>();
        ArrayList<String> JOINS      = new ArrayList<>();
        ArrayList<String> PARAMETERS = new ArrayList<>();

        String SELECT = "SELECT * FROM Hoot h ";
        String JOIN   = "";
        String LIMIT  = "LIMIT " + defaultPageSize;

        if (userId != null) {
            WHERE.add("userId = ?");
            PARAMETERS.add(userId.toString());
        }

        if (tags != null && !tags.equals("")) {
            JOINS.add("LEFT JOIN HootTags ht ON h.id = ht.id");
            JOINS.add("LEFT JOIN Tag t ON ht.tag = t.tag");
            WHERE.add("t.tag IN (?) ");
            PARAMETERS.add(tags);
        }

        if (lastPostId != null) {
            // ID's are incremental, so this is easier than a timestamp comparison
            WHERE.add("h.id < ?");
            PARAMETERS.add(lastPostId.toString());
        }

        if (!withComments) {
            WHERE.add("h.type != ?");
            PARAMETERS.add(HootType.Comment.toString());
        }

        StringBuilder QUERY = new StringBuilder(SELECT);

        for (String join : JOINS) {
            QUERY.append(" ").append(join);
        }

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
