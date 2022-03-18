package hoot.system.Cache;

import hoot.system.Logger.LoggerInterface;
import hoot.system.Redis.RedisManager;
import hoot.system.Serializer.Serializer;
import hoot.system.objects.Inject;

import java.io.IOException;

public abstract class AbstractRedisCache<Type>
{
    public static String PREFIX = "cache-";

    @Inject protected Serializer      serializer;
    @Inject protected LoggerInterface logger;
    @Inject private   RedisManager    redisManager;

    protected abstract String getKey(Type type);

    public Integer countByKeyIdentifier(String keyIdentifier)
    {
        return this.redisManager.countKeysByPattern(PREFIX + keyIdentifier + "*");
    }

    public synchronized void put(String key, Type type)
    {
        if (key == null) {
            return;
        }

        this.redisManager.set(PREFIX + key, this.serializer.serialize(type));
    }

    protected void delete(String key)
    {
        if (key != null) {
            this.redisManager.delete(PREFIX + key);
        }
    }

    protected Object loadRedisResult(String key, Class<Type> targetClass)
    {
        if (key != null) {
            try {
                String redisResult = this.redisManager.get(PREFIX + key);

                if (redisResult == null) {
                    return null;
                }

                return this.serializer.deserialize(redisResult, targetClass);
            } catch (IOException e) {
                this.logger.log("Could not deserialize String to User: " + e.getMessage());
            }
        }

        return null;
    }
}
