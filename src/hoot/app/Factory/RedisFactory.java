package hoot.app.Factory;

import hoot.system.objects.FactoryInterface;
import nl.melp.redis.Redis;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.net.Socket;

public class RedisFactory implements FactoryInterface<Redis>
{
    private final ServletContext context;

    public RedisFactory(ServletContext context)
    {
        this.context = context;
    }

    @Override
    public Redis create()
    {
        try {
            Redis redis = new Redis(new Socket("127.0.0.1", 6379));
            String password = context.getInitParameter("redis-pwd");
            redis.call("AUTH", password);
            return redis;
        } catch (IOException e) {
            this.context.log(e.getMessage());
        }

        return null;
    }
}
