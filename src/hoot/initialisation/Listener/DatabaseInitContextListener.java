package hoot.initialisation.Listener;

import hoot.system.Database.DatabaseConnectionPool;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;
import java.sql.SQLException;

public class DatabaseInitContextListener implements ServletContextListener
{
    public void contextInitialized(ServletContextEvent servletContextEvent)
    {
        ServletContext ctx = servletContextEvent.getServletContext();

        try {
            Context    initCtx = new InitialContext();
            Context    envCtx  = (Context) initCtx.lookup("java:/comp/env");
            DataSource ds      = (DataSource) envCtx.lookup("jdbc/mariadb");

            DatabaseConnectionPool instance = DatabaseConnectionPool.getInstance(ds);
        } catch (SQLException | NamingException e) {
            ctx.log("DatabaseConnectionPool initialisation failed");
            ctx.log(e.getMessage());
        }
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent)
    {
        ServletContext ctx = servletContextEvent.getServletContext();
        ctx.log("destroyed");
    }
}
