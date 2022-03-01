package hoot.system.Logger;

import javax.servlet.ServletContext;

public class ContextLogger implements LoggerInterface, QueryLoggerInterface
{
    private final ServletContext context;

    public ContextLogger(ServletContext context)
    {
        this.context = context;
    }

    @Override
    public void log(String message)
    {
        this.context.log(message);
    }
}
