package hoot.system.Database;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public final class DatabaseConnectionPool
{
    // TODO: find out why only 8 concurrent SQL connections are possible and increase limit
    private static final int                    POOL_SIZE = 8;
    private final        ArrayList<Connection>  pool      = new ArrayList<>();
    private static       DatabaseConnectionPool instance;

    private DatabaseConnectionPool(DataSource ds) throws SQLException
    {
        createConnections(ds);
    }

    public static synchronized DatabaseConnectionPool getPool()
    {
        return instance;
    }

    public static synchronized DatabaseConnectionPool getInstance(DataSource ds) throws SQLException, NamingException
    {
        if (instance == null) {
            instance = new DatabaseConnectionPool(ds);
        }
        return instance;
    }

    private void createConnections(DataSource ds) throws SQLException
    {
        for (int counter = 0; counter < POOL_SIZE; ++counter) {
            Connection connection = ds.getConnection();
            pool.add(connection);
        }
    }

    public synchronized Connection get()
    {
        while (this.pool.isEmpty()) {
            this.doWait();
        }

        Connection connection = pool.remove(0);
        notifyAll();

        return connection;
    }

    public synchronized void put(Connection connection)
    {
        while (POOL_SIZE <= this.pool.size()) {
            this.doWait();
        }

        this.pool.add(connection);
        notifyAll();
    }

    private synchronized void doWait()
    {
        try {
            wait();
        } catch (InterruptedException ignore) {
        }
    }
}
