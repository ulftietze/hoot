package hoot.model.cache;

import hoot.model.entities.Hoot;
import hoot.system.Cache.AbstractCache;

import java.util.HashMap;

public class HootCache extends AbstractCache<Hoot>
{
    private final HashMap<Integer, CacheObject> idLookupMap;

    public HootCache()
    {
        this.idLookupMap = new HashMap<>();
    }

    public synchronized Hoot get(int id)
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