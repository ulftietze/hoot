package hoot.model.monitoring;

import hoot.model.cache.HootCacheInterface;
import hoot.model.cache.UserCacheInterface;
import hoot.system.Exception.CollectorException;
import hoot.system.Monitoring.CollectorInterface;
import hoot.system.Monitoring.CollectorResult;
import hoot.system.objects.Inject;

public class CacheSizeCollector extends Thread implements CollectorInterface
{
    public final static String COLLECTOR_NAME = "Cache Sizes";

    @Inject private UserCacheInterface userCache;

    @Inject private HootCacheInterface hootCache;

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
            put(UserCacheInterface.CACHE_NAME, userCache.getSize());
            put(HootCacheInterface.CACHE_NAME, hootCache.getSize());
        }};
    }
}
