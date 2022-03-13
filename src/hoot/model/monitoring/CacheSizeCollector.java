package hoot.model.monitoring;

import hoot.model.cache.HootCache;
import hoot.model.cache.UserCache;
import hoot.system.Exception.CollectorException;
import hoot.system.Monitoring.CollectorInterface;
import hoot.system.Monitoring.CollectorResult;
import hoot.system.ObjectManager.ObjectManager;

public class CacheSizeCollector extends Thread implements CollectorInterface
{
    public final static String COLLECTOR_NAME = "Cache Sizes";
    public final static String USER_CACHE     = "User Cache";
    public final static String HOOT_CACHE     = "Hoot Cache";

    private final UserCache userCache;

    private final HootCache hootCache;

    public CacheSizeCollector()
    {
        this.userCache = (UserCache) ObjectManager.get(UserCache.class);
        this.hootCache = (HootCache) ObjectManager.get(HootCache.class);
    }

    @Override
    public String getCollectorName()
    {
        return COLLECTOR_NAME;
    }

    @Override
    public CollectorResult collect() throws CollectorException
    {
        return new CollectorResult()
        {{
            put(USER_CACHE, userCache.getSize());
            put(HOOT_CACHE, hootCache.getSize());
        }};
    }
}
