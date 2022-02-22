package hoot.initialisation.Listener;

import hoot.model.repositories.UserRepository;
import hoot.system.Logger.ContextLogger;
import hoot.system.Logger.LoggerInterface;
import hoot.system.ObjectManager.ObjectManager;
import hoot.system.Serializer.RequestSerializer;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;
import java.util.Timer;

public class ObjectInstantiationContextListener implements ServletContextListener
{
    Timer timer;

    public void contextInitialized(ServletContextEvent servletContextEvent)
    {
        ServletContext ctx = servletContextEvent.getServletContext();

        ObjectManager.set(DataSource.class, this.getDataSource(ctx));
        ObjectManager.set(RequestSerializer.class, new RequestSerializer());
        ObjectManager.set(LoggerInterface.class, new ContextLogger(ctx));
        ObjectManager.set(UserRepository.class, new UserRepository());
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent)
    {
    }

    private DataSource getDataSource(ServletContext ctx)
    {
        try {
            Context initCtx = new InitialContext();
            Context envCtx  = (Context) initCtx.lookup("java:/comp/env");
            return (DataSource) envCtx.lookup("jdbc/mariadb");
        } catch (NamingException e) {
            ctx.log(e.getMessage());
        }

        return null;
    }
}
