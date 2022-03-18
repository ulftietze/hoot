package hoot.app.Listener;

import hoot.model.cache.HootCacheInterface;
import hoot.model.cache.UserCacheInterface;
import hoot.system.Cache.CacheManager;
import hoot.system.objects.ObjectManager;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class CacheInstantiationContextListener implements ServletContextListener
{
    /**
     * TODO: Documentation
     */
    public void contextInitialized(ServletContextEvent servletContextEvent)
    {
        CacheManager cacheManager = (CacheManager) ObjectManager.get(CacheManager.class);

        cacheManager.registerCache((HootCacheInterface) ObjectManager.get(HootCacheInterface.class));
        cacheManager.registerCache((UserCacheInterface) ObjectManager.get(UserCacheInterface.class));
    }
}
