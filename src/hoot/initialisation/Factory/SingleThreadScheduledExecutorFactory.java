package hoot.initialisation.Factory;

import hoot.system.ObjectManager.FactoryInterface;

import java.util.concurrent.Executors;

public class SingleThreadScheduledExecutorFactory implements FactoryInterface
{
    @Override
    public Object create()
    {
        return Executors.newSingleThreadScheduledExecutor();
    }
}
