package hoot.initialisation.Factory;

import hoot.system.Logger.LoggerInterface;
import hoot.system.ObjectManager.FactoryInterface;
import hoot.system.ObjectManager.ObjectManager;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class DataSourceFactory implements FactoryInterface<DataSource>
{
    @Override
    public DataSource create()
    {
        try {
            Context initCtx = new InitialContext();
            Context envCtx  = (Context) initCtx.lookup("java:/comp/env");

            return (DataSource) envCtx.lookup("jdbc/mariadb");
        } catch (NamingException ignore) {
            LoggerInterface logger = (LoggerInterface) ObjectManager.get(LoggerInterface.class);
            logger.log("Could not load Database Connection.");
        }

        return null;
    }
}
