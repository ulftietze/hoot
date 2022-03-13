package hoot.system.Cache;

import hoot.system.Logger.LoggerInterface;
import hoot.system.ObjectManager.ObjectManager;
import hoot.system.Redis.RedisManager;
import hoot.system.Serializer.Serializer;

public abstract class AbstractRedisCache<Type>
{
    protected final Serializer      serializer;
    protected final RedisManager    redisManager;
    protected final LoggerInterface logger;

    public AbstractRedisCache()
    {
        this.serializer   = (Serializer) ObjectManager.get(Serializer.class);
        this.redisManager = (RedisManager) ObjectManager.get(RedisManager.class);
        this.logger       = (LoggerInterface) ObjectManager.get(LoggerInterface.class);
    }

    public abstract void put(Type type);
}
