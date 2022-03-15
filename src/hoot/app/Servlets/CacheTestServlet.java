package hoot.app.Servlets;

import hoot.app.Servlets.Api.V1.AbstractApiServlet;
import hoot.model.cache.*;
import hoot.model.repositories.HootRepository;
import hoot.model.repositories.UserRepository;
import hoot.system.Cache.CacheManager;
import hoot.system.Logger.LoggerInterface;
import hoot.system.ObjectManager.ObjectManager;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;

@WebServlet("/api/V1/cache")
public class CacheTestServlet extends AbstractApiServlet
{
    private CacheManager    cacheManager;
    private HootRepository  hootRepository;
    private UserRepository  userRepository;
    private UserCache       userCacheInMemory;
    private UserRedisCache  userCacheRedis;
    private UserEmptyCache  userEmptyCache;
    private HootCache       hootCacheInMemory;
    private HootRedisCache  hootCacheRedis;
    private HootEmptyCache  hootEmptyCache;
    private LoggerInterface logger;

    @Override
    public void init() throws ServletException
    {
        super.init();

        this.cacheManager   = (CacheManager) ObjectManager.get(CacheManager.class);
        this.hootRepository = (HootRepository) ObjectManager.get(HootRepository.class);
        this.userRepository = (UserRepository) ObjectManager.get(UserRepository.class);

        this.userCacheInMemory = (UserCache) ObjectManager.get(UserCache.class);
        this.userCacheRedis    = (UserRedisCache) ObjectManager.get(UserRedisCache.class);
        this.userEmptyCache    = (UserEmptyCache) ObjectManager.get(UserEmptyCache.class);

        this.hootCacheInMemory = (HootCache) ObjectManager.get(HootCache.class);
        this.hootCacheRedis    = (HootRedisCache) ObjectManager.get(HootRedisCache.class);
        this.hootEmptyCache    = (HootEmptyCache) ObjectManager.get(HootEmptyCache.class);

        this.logger = (LoggerInterface) ObjectManager.get(LoggerInterface.class);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        try {
            HashMap<String, Long> res = new HashMap<>();
            int amount                = 1000;

            this.cacheManager.flushCache();
            this.userCacheRedis.clean();
            this.userCacheInMemory.clean();
            this.hootCacheRedis.clean();
            this.hootCacheInMemory.clean();

            // ############################################################### //
            // Redis Cache
            ObjectManager.set(UserCacheInterface.class, this.userCacheRedis);
            ObjectManager.set(HootCacheInterface.class, this.hootCacheRedis);

            // USER
            Instant start = Instant.now();
            for (int i = 1; i <= amount; i++) {
                this.userRepository.getById(i);
            }
            Duration d = Duration.between(start, Instant.now());
            this.logger.log("[REDIS] User -> Load from Database and fill cache: " + d.toMillis());
            res.put        ("[REDIS] User -> Load from Database and fill cache: ", d.toMillis());
            start = Instant.now();
            for (int i = 1; i <= amount; i++) {
                this.userRepository.getById(i);
            }
            d = Duration.between(start, Instant.now());
            this.logger.log("[REDIS] User -> Load from Cache and deserialize: " + d.toMillis());
            res.put        ("[REDIS] User -> Load from Cache and deserialize: ", d.toMillis());

            // Hoot
            start = Instant.now();
            for (int i = 1; i <= amount; i++) {
                this.hootRepository.getById(i);
            }
            d = Duration.between(start, Instant.now());
            this.logger.log("[REDIS] Hoot -> Load from Database and fill cache: " + d.toMillis());
            res.put        ("[REDIS] Hoot -> Load from Database and fill cache: ", d.toMillis());
            start = Instant.now();
            for (int i = 1; i <= amount; i++) {
                this.hootRepository.getById(i);
            }
            d = Duration.between(start, Instant.now());
            this.logger.log("[REDIS] Hoot -> Load from Cache and deserialize: " + d.toMillis());
            res.put        ("[REDIS] Hoot -> Load from Cache and deserialize: ", d.toMillis());
            // ############################################################### //


            // ############################################################### //
            // IN MEMORY Cache
            ObjectManager.set(UserCacheInterface.class, this.userCacheInMemory);
            ObjectManager.set(HootCacheInterface.class, this.hootCacheInMemory);

            // USER
            start = Instant.now();
            for (int i = 1; i <= amount; i++) {
                this.userRepository.getById(i);
            }
            d = Duration.between(start, Instant.now());
            this.logger.log("[IN MEMORY] User -> Load from Database and fill cache: " + d.toMillis());
            res.put        ("[IN MEMORY] User -> Load from Database and fill cache: ", d.toMillis());
            start = Instant.now();
            for (int i = 1; i <= amount; i++) {
                this.userRepository.getById(i);
            }
            d = Duration.between(start, Instant.now());
            this.logger.log("[IN MEMORY] User -> Load from Cache and deserialize: " + d.toMillis());
            res.put        ("[IN MEMORY] User -> Load from Cache and deserialize: ", d.toMillis());

            // Hoot
            start = Instant.now();
            for (int i = 1; i <= amount; i++) {
                this.hootRepository.getById(i);
            }
            d = Duration.between(start, Instant.now());
            this.logger.log("[IN MEMORY] Hoot -> Load from Database and fill cache: " + d.toMillis());
            res.put        ("[IN MEMORY] Hoot -> Load from Database and fill cache: ", d.toMillis());
            start = Instant.now();
            for (int i = 1; i <= amount; i++) {
                this.hootRepository.getById(i);
            }
            d = Duration.between(start, Instant.now());
            this.logger.log("[IN MEMORY] Hoot -> Load from Cache and deserialize: " + d.toMillis());
            res.put        ("[IN MEMORY] Hoot -> Load from Cache and deserialize: ", d.toMillis());
            // ############################################################### //

            // ############################################################### //
            // NO Cache
            ObjectManager.set(UserCacheInterface.class, this.userEmptyCache);
            ObjectManager.set(HootCacheInterface.class, this.hootEmptyCache);

            // USER
            start = Instant.now();
            for (int i = 1; i <= amount; i++) {
                this.userRepository.getById(i);
            }
            d = Duration.between(start, Instant.now());
            this.logger.log("[NO CACHE] User -> Load from Database: " + d.toMillis());
            res.put        ("[NO CACHE] User -> Load from Database: ", d.toMillis());

            // Hoot
            start = Instant.now();
            for (int i = 1; i <= amount; i++) {
                this.hootRepository.getById(i);
            }
            d = Duration.between(start, Instant.now());
            this.logger.log("[NO CACHE] Hoot -> Load from Database: " + d.toMillis());
            res.put        ("[NO CACHE] Hoot -> Load from Database: ", d.toMillis());
            // ############################################################### //

            this.sendResponse(response, HttpServletResponse.SC_OK, this.serialize(res));
        } catch (Throwable e) {
            throw new ServletException("Noped out: " + e.getMessage());
        }
    }
}
