package hoot.model.query.api;

import hoot.front.api.dto.authentication.LoginDTO;
import hoot.model.query.GetStringHashed;
import hoot.system.Database.DatabaseConnectionPool;

import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GetUserIdIfValidLogin
{
    private final DatabaseConnectionPool databasePool;

    private final GetStringHashed getStringHashed;

    public GetUserIdIfValidLogin()
    {
        this.databasePool    = DatabaseConnectionPool.getPool();
        this.getStringHashed = new GetStringHashed();
    }

    /**
     * This class loads the
     *
     * @param login The LoginDTO which is used to transport data via the API
     * @return The UserId if the login data matches the database data
     */
    public Integer execute(LoginDTO login)
    {
        // TODO: Get Database Connection from Pool
        // TODO: Hash Password
        // TODO: Load User by Credentials
        // TODO: validate and return if user exists

        try {
            PreparedStatement statement = this.getStatementForLogin(login);
            ResultSet         result    = statement.executeQuery();

            result.next();

            return result.getInt("id");

        } catch (SQLException e) {
            return null;
        } catch (GeneralSecurityException e) {
            // TODO: Implement Logging
            return null;
        }
    }

    private PreparedStatement getStatementForLogin(LoginDTO login) throws SQLException, GeneralSecurityException
    {
        String            query      = "SELECT id, username FROM User WHERE username = ? AND passwordHash = ?";
        Connection        connection = databasePool.get();
        PreparedStatement statement  = connection.prepareStatement(query);

        statement.setString(1, login.username);
        statement.setString(2, this.getStringHashed.execute(login.password));

        return statement;
    }
}
