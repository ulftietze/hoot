package hoot.system.Redis;

import hoot.system.Logger.LoggerInterface;
import hoot.system.objects.Inject;
import nl.melp.redis.Redis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RedisManager
{
    private final static int EXPIRE_IN_SECONDS = 60 * 60 * 6;

    @Inject private Redis           redisInstance;
    @Inject private LoggerInterface logger;

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

    public synchronized Integer countKeysByPattern(String keyPattern)
    {
        return this.getKeysByPattern(keyPattern).size();
    }

    public synchronized List<String> getKeysByPattern(String keyPattern)
    {
        if (keyPattern == null || keyPattern.equals("")) {
            keyPattern = "*";
        }

        List<String> matchingKeys = new ArrayList<>();

        try {
            List<Object> res = this.redisInstance.call("KEYS", keyPattern);

            if (res == null) {
                return null;
            }

            for (var r : res) {
                matchingKeys.add(new String((byte[]) r));
            }
        } catch (IOException e) {
            this.logger.logException("Could not load keys by pattern " + keyPattern + ": " + e.getMessage(), e);
        }

        return matchingKeys;
    }

    public synchronized void delete(String key)
    {
        try {
            this.redisInstance.call("DEL", key);
        } catch (IOException e) {
            this.logger.log("Deleting of Redis Key/Value was not possible: " + e.getMessage());
        }
    }

    public synchronized void delete(List<String> keys)
    {
        keys.forEach(this::delete);
    }
}
