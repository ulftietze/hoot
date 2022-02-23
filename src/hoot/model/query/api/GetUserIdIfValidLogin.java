package hoot.model.query.api;

import hoot.front.api.dto.authentication.LoginDTO;
import hoot.model.query.GetStringHashed;
import hoot.system.Logger.LoggerInterface;
import hoot.system.ObjectManager.ObjectManager;

import javax.sql.DataSource;
import java.security.GeneralSecurityException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GetUserIdIfValidLogin
{
    private final DataSource dataSource;

    private final GetStringHashed getStringHashed;

    LoggerInterface logger;

    public GetUserIdIfValidLogin()
    {
        this.dataSource      = (DataSource) ObjectManager.get(DataSource.class);
        this.getStringHashed = (GetStringHashed) ObjectManager.get(GetStringHashed.class);
        this.logger          = (LoggerInterface) ObjectManager.get(LoggerInterface.class);
    }

    /**
     * // TODO: Return the User Entity
     *
     * @param login The LoginDTO which is used to transport data via the API
     * @return The UserId if the login data matches the database data
     */
    public Integer execute(LoginDTO login)
    {
        try {
            ResultSet result = this.loadUserRowFromDatabase(login);
            result.next();

            return result.getInt("id");
        } catch (SQLException e) {
            // Ignore, in this case the credentials are simply wrong.
            return null;
        } catch (GeneralSecurityException e) {
            this.logger.log("Unable to Login User. " + e.getMessage());
            return null;
        }
    }

    private ResultSet loadUserRowFromDatabase(LoginDTO login) throws SQLException, GeneralSecurityException
    {
        String            query     = "SELECT id, username FROM User WHERE username = ? AND passwordHash = ?";
        PreparedStatement statement = dataSource.getConnection().prepareStatement(query);

        statement.setString(1, login.username);
        statement.setString(2, this.getStringHashed.execute(login.password));

        return statement.executeQuery();
    }
}
