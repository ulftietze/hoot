package hoot.app.Factory;

import hoot.system.objects.FactoryInterface;

public class ProcessBuilderFactory implements FactoryInterface<ProcessBuilder>
{
    @Override
    public ProcessBuilder create()
    {
        return new ProcessBuilder();
    }
}
