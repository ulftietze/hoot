package hoot.model.cache;

import hoot.model.entities.User;
import hoot.system.Cache.CacheInterface;

public interface UserCacheInterface extends CacheInterface<User>
{
    String CACHE_NAME = "User Cache";

    String CACHE_IDENTIFIER_PREFIX = "user-";

    default String getIdentifier()
    {
        return CACHE_IDENTIFIER_PREFIX;
    }

    User get(String username);
}
