package hoot.initialisation.Factory;

import hoot.system.Logger.ContextLogger;
import hoot.system.Logger.LoggerInterface;
import hoot.system.ObjectManager.FactoryInterface;

import javax.servlet.ServletContext;

public class ContextLoggerFactory implements FactoryInterface<LoggerInterface>
{
    private final ServletContext context;

    public ContextLoggerFactory(ServletContext context)
    {
        this.context = context;
    }

    @Override
    public LoggerInterface create()
    {
        return new ContextLogger(this.context);
    }
}
