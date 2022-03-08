package hoot.system.Logger;

import javax.servlet.ServletContext;
import java.util.Arrays;
import java.util.stream.Collectors;

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

    @Override
    public void logException(String message, Throwable e)
    {
        String trace = Arrays
                .stream(e.getStackTrace())
                .map(StackTraceElement::toString)
                .collect(Collectors.joining("\n"));

        this.log(e.getClass().getName() + ": " + message + "\n" + trace);
    }
}
