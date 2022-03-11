package hoot.model.monitoring;

import hoot.model.cache.UserCache;
import hoot.system.Exception.CollectorException;
import hoot.system.Monitoring.CollectorInterface;
import hoot.system.Monitoring.CollectorResult;
import hoot.system.ObjectManager.ObjectManager;
import hoot.system.Queue.QueueManager;

public class CacheSizeCollector extends Thread implements CollectorInterface
{
    public final static String COLLECTOR_NAME = "Cache Sizes";

    private final UserCache userCache;

    public CacheSizeCollector()
    {
        this.userCache = (UserCache) ObjectManager.get(UserCache.class);
    }

    @Override
    public String getCollectorName()
    {
        return COLLECTOR_NAME;
    }

    @Override
    public CollectorResult collect() throws CollectorException
    {
        return new CollectorResult() {{
            put("User Cache", userCache.getSize());
        }};
    }
}
