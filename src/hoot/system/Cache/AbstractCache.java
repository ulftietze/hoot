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
    protected static final Long            hoursUntilDestroy = 6L;
    protected final        LoggerInterface logger;

    protected final TreeMap<LocalDateTime, ArrayList<CacheObject>> timedDeleteMap = new TreeMap<>();

    public AbstractCache()
    {
        this.logger = (LoggerInterface) ObjectManager.get(LoggerInterface.class);

        // TODO: We need to ensure that this executor is shut-down properly when context is destroyed
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(this::clearCache, hoursUntilDestroy * 4 * 15, 15, TimeUnit.MINUTES);
    }

    public abstract void put(Type type);

    public abstract void purge(Type type);

    public abstract Integer getSize();

    protected synchronized Type getTypeFromCacheObject(CacheObject cacheObject)
    {
        if (cacheObject != null) {
            this.timedDeleteMap.get(cacheObject.getDestroyTimestamp()).remove(cacheObject);
            Type object = cacheObject.getObject();
            this.putInTimedDeleteMap(cacheObject);
            return object;
        } else {
            return null;
        }
    }

    protected synchronized void putInTimedDeleteMap(CacheObject cacheObject)
    {
        this.timedDeleteMap.computeIfAbsent(cacheObject.getDestroyTimestamp(), k -> new ArrayList<>());
        this.timedDeleteMap.get(cacheObject.getDestroyTimestamp()).add(cacheObject);
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

    protected class CacheObject
    {
        private final Type          object;
        private       LocalDateTime destroyTimestamp;

        public CacheObject(Type object)
        {
            this.object           = object;
            this.destroyTimestamp = LocalDateTime.now().plusHours(AbstractCache.hoursUntilDestroy);
        }

        public Type getObject()
        {
            this.destroyTimestamp = this.destroyTimestamp.plusHours(AbstractCache.hoursUntilDestroy);
            return this.object;
        }

        public LocalDateTime getDestroyTimestamp()
        {
            return this.destroyTimestamp;
        }
    }
}