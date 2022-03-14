package hoot.system.Cache;

import hoot.system.ObjectManager.ObjectManager;
import hoot.system.Redis.RedisManager;

import java.util.List;
import java.util.TreeMap;

public class CacheManager
{
    private final RedisManager redisManager;

    private TreeMap<String, CacheInterface<?>> caches;

    public CacheManager()
    {
        this.redisManager = (RedisManager) ObjectManager.get(RedisManager.class);
        this.caches       = new TreeMap<>();
    }

    public void registerCache(CacheInterface<?> cache)
    {
        this.caches.put(cache.getIdentifier(), cache);
    }

    public void flushCache()
    {
        List<String> cacheKeys = this.redisManager.getKeysByPattern(AbstractRedisCache.PREFIX + "*");
        this.redisManager.delete(cacheKeys);
        this.caches.forEach((s, c) -> c.clean());
    }

    public void cleanCache(String ...cacheIdentifier)
    {
        if (cacheIdentifier.length == 0) {
            this.caches.forEach((s, c) -> this.redisManager.delete(AbstractRedisCache.PREFIX + c.getIdentifier()));
            return;
        }

        for (String cacheId: cacheIdentifier) {
            if (this.caches.get(cacheId) == null) {
                continue;
            }

            this.caches.get(cacheId).clean();
        }
    }
}
