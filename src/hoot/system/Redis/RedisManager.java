package hoot.system.Redis;

import hoot.system.Logger.LoggerInterface;
import hoot.system.ObjectManager.ObjectManager;
import nl.melp.redis.Redis;

import java.io.IOException;

public class RedisManager
{
    private final Redis redisInstance;
    private final LoggerInterface logger;

    private final static int EXPIRE_IN_SECONDS = 60 * 60 * 6;

    public RedisManager()
    {
        this.redisInstance = (Redis) ObjectManager.get(Redis.class);
        this.logger = (LoggerInterface) ObjectManager.get(LoggerInterface.class);
    }

    public synchronized boolean set(String key, String value)
    {
        try {
            this.redisInstance.call("SET", key, value);
            this.redisInstance.call("EXPIRE", key, "" + EXPIRE_IN_SECONDS);
            return true;
        } catch (IOException e) {
            this.logger.log("Setting of Redis Key/Value was not possible: " + e.getMessage());
            return false;
        }
    }

    public synchronized String get(String key)
    {
        try {
            var res = this.redisInstance.call("GET", key);
            if (res == null) {
                return null;
            }
            this.redisInstance.call("EXPIRE", key, "" + EXPIRE_IN_SECONDS);
            return new String((byte[]) res);
        } catch (IOException e) {
            this.logger.log("Getting of Redis Key/Value was not possible: " + e.getMessage());
            return null;
        }
    }

    public synchronized void delete(String key)
    {
        try {
            this.redisInstance.call("DEL", key);
        } catch (IOException e) {
            this.logger.log("Deleting of Redis Key/Value was not possible: " + e.getMessage());
        }
    }
}
