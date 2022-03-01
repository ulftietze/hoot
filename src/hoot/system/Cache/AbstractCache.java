package hoot.system.Cache;

import hoot.system.Logger.LoggerInterface;
import hoot.system.ObjectManager.ObjectManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class AbstractCache<Type>
{
    protected static final Long hoursUntilDestroy = 6L;

    protected final TreeMap<LocalDateTime, ArrayList<CacheObject>> timedDeleteMap = new TreeMap<>();

    public AbstractCache()
    {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(this::clearCache, hoursUntilDestroy * 4 * 15, 15, TimeUnit.MINUTES);
    }

    public abstract void put(Type type);

    protected synchronized Type getTypeFromCacheObject(CacheObject cacheObject)
    {
        if (cacheObject != null) {
            timedDeleteMap.get(cacheObject.getDestroy()).remove(cacheObject);
            Type user = cacheObject.getObject();
            this.putInTimedDeleteMap(cacheObject);
            return user;
        } else {
            return null;
        }
    }

    protected synchronized void putInTimedDeleteMap(CacheObject cacheObject)
    {
        if (!timedDeleteMap.containsKey(cacheObject.getDestroy())) {
            timedDeleteMap.put(cacheObject.getDestroy(), new ArrayList<>());
        }
        timedDeleteMap.get(cacheObject.getDestroy()).add(cacheObject);
    }

    protected synchronized void clearCache()
    {
        var map = this.timedDeleteMap.headMap(LocalDateTime.now(), true);
        map.forEach((LocalDateTime time, ArrayList<CacheObject> list) -> list.forEach((CacheObject cacheObject) -> {
            this.removeReferences(cacheObject.getObject());
        }));
        map.clear();
        System.gc();
    }

    protected abstract void removeReferences(Type type);

    protected synchronized void log(String message)
    {
        LoggerInterface logger = (LoggerInterface) ObjectManager.get(LoggerInterface.class);
        logger.log(message);
    }

    protected class CacheObject
    {
        private final Type          object;
        private       LocalDateTime destroy;

        public CacheObject(Type object)
        {
            this.object  = object;
            this.destroy = LocalDateTime.now().plusHours(AbstractCache.hoursUntilDestroy);
        }

        public Type getObject()
        {
            this.destroy = this.destroy.plusHours(AbstractCache.hoursUntilDestroy);
            return this.object;
        }

        public LocalDateTime getDestroy()
        {
            return this.destroy;
        }
    }
}