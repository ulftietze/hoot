package hoot.app.Listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import hoot.app.Factory.*;
import hoot.model.cache.*;
import hoot.system.Filesystem.FileHandler;
import hoot.system.Filesystem.MediaFileHandler;
import hoot.system.Logger.ContextLogger;
import hoot.system.Logger.LoggerInterface;
import hoot.system.Logger.NullLogger;
import hoot.system.Logger.QueryLoggerInterface;
import hoot.system.ObjectManager.ObjectManager;
import nl.melp.redis.Redis;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;
import java.util.concurrent.ScheduledExecutorService;

@WebListener
public class ObjectInstantiationContextListener implements ServletContextListener
{
    /**
     * TODO: Documentation
     */
    public void contextInitialized(ServletContextEvent servletContextEvent)
    {
        servletContextEvent.getServletContext().log("init ObjectInstantiationContextListener");
        ServletContext context = servletContextEvent.getServletContext();

        // System
        ObjectManager.set(LoggerInterface.class, ContextLogger.class);
        ObjectManager.set(QueryLoggerInterface.class, NullLogger.class);
        ObjectManager.setFactory(ContextLogger.class, new ContextLoggerFactory(context));
        //ObjectManager.set(LoggerInterface.class, NullLogger.class);
        ObjectManager.setFactory(DataSource.class, new DataSourceFactory());
        ObjectManager.setFactory(FileHandler.class, new FileHandlerFactory(context));
        ObjectManager.setFactory(MediaFileHandler.class, new MediaFileHandlerFactory(context));
        ObjectManager.setFactory(ObjectMapper.class, new JacksonSerializerFactory());
        ObjectManager.setFactory(ScheduledExecutorService.class, new SingleThreadScheduledExecutorFactory());
        ObjectManager.setFactory(ProcessBuilder.class, new ProcessBuilderFactory());
        ObjectManager.setFactory(Redis.class, new RedisFactory(context));

        // Cache
        //ObjectManager.set(UserCacheInterface.class, UserCache.class);
        ObjectManager.set(UserCacheInterface.class, UserRedisCache.class);
        ObjectManager.set(HootCacheInterface.class, HootCache.class);
        //ObjectManager.set(HootCacheInterface.class, HootRedisCache.class);
    }
}
