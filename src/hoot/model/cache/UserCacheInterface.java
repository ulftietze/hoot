package hoot.model.cache;

import hoot.model.entities.User;
import hoot.system.Cache.CacheInterface;

public interface UserCacheInterface extends CacheInterface<User>
{
    String CACHE_NAME = "User Cache";

    User get(String username);
}
