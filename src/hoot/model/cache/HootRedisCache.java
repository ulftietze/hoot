package hoot.model.cache;

import hoot.model.entities.Hoot;
import hoot.system.Cache.AbstractRedisCache;

import java.util.HashMap;

public class HootRedisCache extends AbstractRedisCache<Hoot> implements HootCacheInterface
{
    private final HashMap<Integer, String> idToKeyMap = new HashMap<>();

    @Override
    public synchronized Hoot get(Integer id)
    {
        String key = this.idToKeyMap.get(id);
        return (Hoot) this.loadRedisResult(key, Hoot.class);
    }

    @Override
    public synchronized void purge(Hoot hoot)
    {
        String key = this.getKey(hoot);

        if (key != null) {
            this.idToKeyMap.remove(hoot.id);
            super.delete(key);
        }
    }

    @Override
    public void clean()
    {
        this.delete(this.getIdentifier());
        this.idToKeyMap.clear();
    }

    @Override
    public Integer getSize()
    {
        return this.countByKeyIdentifier(this.getIdentifier());
    }

    @Override
    public synchronized void put(Hoot hoot)
    {
        String key = this.getKey(hoot);

        super.put(key, hoot);
        this.idToKeyMap.put(hoot.id, key);
    }

    protected String getKey(Hoot hoot)
    {
        if (hoot != null && hoot.id != null) {
            return this.getIdentifier() + "-" + hoot.id;
        }

        return null;
    }
}