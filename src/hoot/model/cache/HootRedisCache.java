package hoot.model.cache;

import hoot.model.entities.Hoot;
import hoot.system.Cache.AbstractRedisCache;

import java.util.HashMap;

public class HootRedisCache extends AbstractRedisCache<Hoot> implements HootCacheInterface
{
    @Override
    public synchronized Hoot get(Integer id)
    {
        return (Hoot) this.loadRedisResult(this.getIdentifier() + "-" + id, Hoot.class);
    }

    @Override
    public synchronized void put(Hoot hoot)
    {
        super.put(this.getKey(hoot), hoot);
    }

    @Override
    public synchronized void purge(Hoot hoot)
    {
        super.delete(this.getKey(hoot));
    }

    @Override
    public void clean()
    {
        this.delete(this.getIdentifier());
    }

    @Override
    public Integer getSize()
    {
        return this.countByKeyIdentifier(this.getIdentifier());
    }

    protected String getKey(Hoot hoot)
    {
        if (hoot != null && hoot.id != null) {
            return this.getIdentifier() + "-" + hoot.id;
        }

        return null;
    }
}