package hoot.model.cache;

import hoot.model.entities.User;
import hoot.system.Cache.AbstractRedisCache;

import java.io.IOException;
import java.util.HashMap;

public class UserRedisCache extends AbstractRedisCache<User>
{
    private HashMap<Integer, String> idToKeyMap       = new HashMap<>();
    private HashMap<String, String>  usernameToKeyMap = new HashMap<>();

    public synchronized User get(int id)
    {
        String key = this.idToKeyMap.get(id);
        return this.deserializeKey(key);
    }

    public synchronized User get(String username)
    {
        String key = this.usernameToKeyMap.get(username);
        return this.deserializeKey(key);
    }

    private User deserializeKey(String key)
    {
        if (key != null) {
            try {
                String redisResult = this.redisManager.get(key);
                if (redisResult == null) {
                    return null;
                }
                return (User) this.serializer.deserialize(redisResult, User.class);
            } catch (IOException e) {
                this.logger.log("Could not deserialize String to User: " + e.getMessage());
            }
        }
        return null;
    }

    public synchronized void purge(User user)
    {
        String key = this.getKey(user);
        if (key != null) {
            this.idToKeyMap.remove(user.id);
            this.usernameToKeyMap.remove(user.username);
            this.redisManager.delete(key);
        }
    }

    @Override
    public synchronized void put(User user)
    {
        String key = this.getKey(user);

        if (key == null) {
            return;
        }

        this.redisManager.set(key, this.serializer.serialize(user));
        this.idToKeyMap.put(user.id, key);
        this.usernameToKeyMap.put(user.username, key);
    }

    private String getKey(User user)
    {
        if (user != null && user.id != null) {
            return "user-" + user.id;
        } else {
            return null;
        }
    }
}