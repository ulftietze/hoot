package hoot.initialisation.Listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import hoot.initialisation.Factory.ContextLoggerFactory;
import hoot.initialisation.Factory.DataSourceFactory;
import hoot.initialisation.Factory.JacksonSerializerFactory;
import hoot.initialisation.Factory.MediaFileHandlerFactory;
import hoot.system.Filesystem.MediaFileHandler;
import hoot.system.Logger.ContextLogger;
import hoot.system.Logger.LoggerInterface;
import hoot.system.Logger.NullLogger;
import hoot.system.Logger.QueryLoggerInterface;
import hoot.system.ObjectManager.ObjectManager;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;
import java.util.Timer;

public class ObjectInstantiationContextListener implements ServletContextListener
{
    Timer timer;

    /**
     * TODO: Documentation
     */
    public void contextInitialized(ServletContextEvent servletContextEvent)
    {
        ServletContext context = servletContextEvent.getServletContext();

        // System
        ObjectManager.set(LoggerInterface.class, ContextLogger.class);
        ObjectManager.set(QueryLoggerInterface.class, NullLogger.class);
        ObjectManager.setFactory(ContextLogger.class, new ContextLoggerFactory(context));
        //ObjectManager.set(LoggerInterface.class, NullLogger.class);
        ObjectManager.setFactory(DataSource.class, new DataSourceFactory());
        ObjectManager.setFactory(MediaFileHandler.class, new MediaFileHandlerFactory(context));
        ObjectManager.setFactory(ObjectMapper.class, new JacksonSerializerFactory());
    }
}
