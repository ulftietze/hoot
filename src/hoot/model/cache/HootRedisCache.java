package hoot.model.cache;

import hoot.model.entities.Hoot;
import hoot.system.Cache.AbstractRedisCache;

import java.io.IOException;
import java.util.HashMap;

public class HootRedisCache extends AbstractRedisCache<Hoot> implements HootCacheInterface
{
    private final HashMap<Integer, String> idToKeyMap = new HashMap<>();

    @Override
    public synchronized Hoot get(Integer id)
    {
        String key = this.idToKeyMap.get(id);
        return this.deserializeKey(key);
    }

    @Override
    public synchronized void purge(Hoot hoot)
    {
        String key = this.getKey(hoot);

        if (key != null) {
            this.idToKeyMap.remove(hoot.id);
            this.redisManager.delete(key);
        }
    }

    @Override
    public Integer getSize()
    {
        return 0;
    }

    @Override
    public synchronized void put(Hoot hoot)
    {
        String key = this.getKey(hoot);

        if (key == null) {
            return;
        }

        this.redisManager.set(key, this.serializer.serialize(hoot));
        this.idToKeyMap.put(hoot.id, key);
    }

    private Hoot deserializeKey(String key)
    {
        if (key != null) {
            try {
                String redisResult = this.redisManager.get(key);

                if (redisResult == null) {
                    return null;
                }

                return (Hoot) this.serializer.deserialize(redisResult, Hoot.class);
            } catch (IOException e) {
                this.logger.log("Could not deserialize String to User: " + e.getMessage());
            }
        }

        return null;
    }

    private String getKey(Hoot hoot)
    {
        if (hoot != null && hoot.id != null) {
            return "hoot-" + hoot.id;
        }

        return null;
    }
}