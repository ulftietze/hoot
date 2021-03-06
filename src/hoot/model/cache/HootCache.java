package hoot.model.cache;

import hoot.model.entities.Hoot;
import hoot.system.Cache.AbstractCache;

import java.util.HashMap;

public class HootCache extends AbstractCache<Hoot> implements HootCacheInterface
{
    private final HashMap<Integer, CacheObject> idLookupMap;

    public HootCache()
    {
        this.idLookupMap = new HashMap<>();
    }

    @Override
    public synchronized Hoot get(Integer id)
    {
        CacheObject cacheObject = this.idLookupMap.get(id);
        return this.getTypeFromCacheObject(cacheObject);
    }

    @Override
    public synchronized void put(Hoot hoot)
    {
        if (hoot == null) {
            return;
        }

        this.purge(hoot);

        CacheObject cacheObject = new CacheObject(hoot);
        this.putInTimedDeleteMap(cacheObject);

        this.idLookupMap.put(hoot.id, cacheObject);
    }

    @Override
    public void purge(Hoot hoot)
    {
        CacheObject cacheObject = this.idLookupMap.get(hoot.id);
        
        if (cacheObject != null) {
            this.timedDeleteMap.get(cacheObject.getDestroyTimestamp()).remove(cacheObject);
            this.removeReferences(cacheObject.getObject());
        }
    }

    @Override
    public void clean()
    {
        this.idLookupMap.forEach((k, c) -> this.timedDeleteMap.get(c.getDestroyTimestamp()).remove(c));
        this.idLookupMap.clear();
    }

    @Override
    public Integer getSize()
    {
        return this.idLookupMap.size();
    }

    @Override
    protected synchronized void removeReferences(Hoot hoot)
    {
        this.idLookupMap.remove(hoot.id);
    }
}