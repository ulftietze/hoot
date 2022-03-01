package hoot.system.Cache;

import hoot.model.entities.User;

import java.util.HashMap;

public class UserCache extends AbstractCache<User>
{
    private final HashMap<Integer, CacheObject> idLookupMap;
    private final HashMap<String, CacheObject>  usernameLookupMap;

    public UserCache()
    {
        this.idLookupMap       = new HashMap<>();
        this.usernameLookupMap = new HashMap<>();
    }

    public synchronized User get(int id)
    {
        CacheObject cacheObject = idLookupMap.get(id);
        return this.getTypeFromCacheObject(cacheObject);
    }

    public synchronized User get(String username)
    {
        CacheObject cacheObject = usernameLookupMap.get(username);
        return this.getTypeFromCacheObject(cacheObject);
    }

    @Override
    public synchronized void put(User user)
    {
        if (user == null || this.get(user.id) != null) {
            return;
        }

        CacheObject cacheObject = new CacheObject(user);
        this.putInTimedDeleteMap(cacheObject);

        idLookupMap.put(user.id, cacheObject);
        usernameLookupMap.put(user.username, cacheObject);
    }

    @Override
    protected synchronized void removeReferences(User user)
    {
        idLookupMap.remove(user.id);
        usernameLookupMap.remove(user.username);
    }
}