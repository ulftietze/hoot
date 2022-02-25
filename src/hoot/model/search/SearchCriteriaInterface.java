package hoot.model.search;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface SearchCriteriaInterface
{
    public PreparedStatement getQueryStatement(Connection connection) throws SQLException;
}
