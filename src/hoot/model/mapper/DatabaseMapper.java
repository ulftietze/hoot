package hoot.model.mapper;

import hoot.model.entities.User;
import hoot.system.Exception.EntityNotFoundException;
import hoot.system.Logger.ContextLogger;
import hoot.system.Logger.LoggerInterface;
import hoot.system.ObjectManager.ObjectManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class DatabaseMapper
{
    private final DataSource    ds;
    private final ContextLogger logger;

    private static DatabaseMapper instance;

    public DatabaseMapper(DataSource ds, ContextLogger logger)
    {
        this.ds     = ds;
        this.logger = logger;
    }

    public static synchronized DatabaseMapper getInstance()
    {
        if (DatabaseMapper.instance == null) {
            DataSource    ds     = (DataSource) ObjectManager.get(DataSource.class);
            ContextLogger logger = (ContextLogger) ObjectManager.get(LoggerInterface.class);
            DatabaseMapper.instance = new DatabaseMapper(ds, logger);
        }
        return DatabaseMapper.instance;
    }

    private static synchronized Connection getConnection() throws SQLException
    {
        DatabaseMapper dm = DatabaseMapper.getInstance();
        return dm.ds.getConnection();
    }

    private static synchronized void log(String message)
    {
        DatabaseMapper dm = DatabaseMapper.getInstance();
        dm.logger.log(message);
    }

    /**
     * Try to return a User object representing the database entry with the given id.
     *
     * @param id User.id (Primary Key)
     * @return User object if the user was found and no SQL errors occurred, null otherwise
     */
    // TODO: Check if synchronisation is required!
    public static User getUserById(int id)
    {
        try {
            Connection connection = DatabaseMapper.getConnection();

            PreparedStatement pss = connection.prepareStatement("select * from User where id = ?");
            pss.setInt(1, id);
            ResultSet rs = pss.executeQuery();

            rs.next();          // will throw SQLException if result set is empty
            if (!rs.isLast()) { // throw Exception if result set contains more than one result
                throw new EntityNotFoundException("User with id " + id);
            }

            if (id != rs.getInt(id)) {
                throw new EntityNotFoundException("User with id " + id);
            }

            LocalDateTime lastLogin = rs
                    .getTimestamp("lastLogin")
                    .toInstant()
                    .atZone(ZoneId.of("Europe/Berlin"))
                    .toLocalDateTime();

            LocalDateTime created = rs
                    .getTimestamp("created")
                    .toInstant()
                    .atZone(ZoneId.of("Europe/Berlin"))
                    .toLocalDateTime();

            User user = new User(id, lastLogin, created);

            user.username     = rs.getString("username");
            user.imagePath    = rs.getString("imagePath");
            user.passwordHash = rs.getString("passwordHash");

            rs.close();
            pss.close();
            connection.close();

            return user;
        } catch (EntityNotFoundException | SQLException e) {
            DatabaseMapper.log(e.getMessage());
            return null;
        }
    }

    public static User createUser()
    {
        // Generate random but unique userName and save it
        // insert generated username in table and check for SQL Errors (username already exists?)
        // search table for username (only one result, username is unique!)
        // call getUserById with the id that was returned by the DB
        // set username, password, etc.
        // return user;
        return null;
    }
}
