package hoot.model.cache;

import hoot.model.entities.User;
import hoot.system.Cache.AbstractRedisCache;

import java.util.HashMap;

public class UserRedisCache extends AbstractRedisCache<User> implements UserCacheInterface
{
    private final HashMap<Integer, String> idToKeyMap       = new HashMap<>();
    private final HashMap<String, String>  usernameToKeyMap = new HashMap<>();

    @Override
    public synchronized User get(Integer id)
    {
        String key = this.idToKeyMap.get(id);
        return (User) this.loadRedisResult(key, User.class);
    }

    @Override
    public synchronized User get(String username)
    {
        String key = this.usernameToKeyMap.get(username);
        return (User) this.loadRedisResult(key, User.class);
    }

    @Override
    public synchronized void purge(User user)
    {
        String key = this.getKey(user);
        if (key != null) {
            this.idToKeyMap.remove(user.id);
            this.usernameToKeyMap.remove(user.username);
            super.delete(key);
        }
    }

    @Override
    public void clean()
    {
        this.delete(this.getIdentifier());
        this.idToKeyMap.clear();
        this.usernameToKeyMap.clear();
    }

    @Override
    public Integer getSize()
    {
        return this.countByKeyIdentifier(this.getIdentifier());
    }

    @Override
    public synchronized void put(User user)
    {
        String key = this.getKey(user);

        super.put(key, user);
        this.idToKeyMap.put(user.id, key);
        this.usernameToKeyMap.put(user.username, key);
    }

    protected String getKey(User user)
    {
        if (user != null && user.id != null) {
            return this.getIdentifier() + "-" + user.id;
        } else {
            return null;
        }
    }
}