package hoot.app.Factory;

import hoot.system.Logger.LoggerInterface;
import hoot.system.objects.FactoryInterface;
import hoot.system.objects.ObjectManager;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class DataSourceFactory implements FactoryInterface<DataSource>
{
    private final LoggerInterface logger;

    public DataSourceFactory()
    {
        this.logger = (LoggerInterface) ObjectManager.get(LoggerInterface.class);
    }

    @Override
    public DataSource create()
    {
        try {
            Context initCtx = new InitialContext();
            Context envCtx  = (Context) initCtx.lookup("java:/comp/env");

            return (DataSource) envCtx.lookup("jdbc/mariadb");
        } catch (NamingException e) {
            this.logger.logException("Could not load Database Connection: " + e.getMessage(), e);
        }

        return null;
    }
}
