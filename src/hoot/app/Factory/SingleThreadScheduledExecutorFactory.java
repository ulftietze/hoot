package hoot.app.Factory;

import hoot.system.objects.FactoryInterface;

import java.util.concurrent.Executors;

public class SingleThreadScheduledExecutorFactory implements FactoryInterface
{
    @Override
    public Object create()
    {
        return Executors.newSingleThreadScheduledExecutor();
    }
}
