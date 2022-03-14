package hoot.model.cache;

import hoot.model.entities.Hoot;
import hoot.system.Cache.CacheInterface;

public interface HootCacheInterface extends CacheInterface<Hoot>
{
    String CACHE_NAME = "Hoot Cache";

    String CACHE_IDENTIFIER_PREFIX = "hoot-";

    default String getIdentifier()
    {
        return CACHE_IDENTIFIER_PREFIX;
    }
}
