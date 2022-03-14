package hoot.model.cache;

import hoot.model.entities.User;
import hoot.system.Cache.AbstractCache;

import java.util.HashMap;

public class UserCache extends AbstractCache<User> implements UserCacheInterface
{
    private final HashMap<Integer, CacheObject> idLookupMap;
    private final HashMap<String, CacheObject>  usernameLookupMap;

    public UserCache()
    {
        this.idLookupMap       = new HashMap<>();
        this.usernameLookupMap = new HashMap<>();
    }

    @Override
    public synchronized User get(Integer id)
    {
        CacheObject cacheObject = this.idLookupMap.get(id);
        return this.getTypeFromCacheObject(cacheObject);
    }

    @Override
    public synchronized User get(String username)
    {
        CacheObject cacheObject = this.usernameLookupMap.get(username);
        return this.getTypeFromCacheObject(cacheObject);
    }

    @Override
    public synchronized void put(User user)
    {
        if (user == null) {
            return;
        }

        this.purge(user);

        CacheObject cacheObject = new CacheObject(user);
        this.putInTimedDeleteMap(cacheObject);

        this.idLookupMap.put(user.id, cacheObject);
        this.usernameLookupMap.put(user.username, cacheObject);
    }

    @Override
    public void purge(User user)
    {
        CacheObject cacheObject = this.idLookupMap.get(user.id);

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
        this.usernameLookupMap.clear();
    }

    @Override
    public Integer getSize()
    {
        return this.idLookupMap.size();
    }

    @Override
    protected synchronized void removeReferences(User user)
    {
        this.idLookupMap.remove(user.id);
        this.usernameLookupMap.remove(user.username);
    }
}