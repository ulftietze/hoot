package hoot.model.monitoring;

import hoot.system.Exception.CollectorException;
import hoot.system.Monitoring.CollectorInterface;

import java.util.HashMap;

public class SystemWorkloadCollector implements CollectorInterface
{
    public final static String COLLECTOR_NAME = "System Workload";

    @Override
    public String getCollectorName()
    {
        return COLLECTOR_NAME;
    }

    @Override
    public Object collect() throws CollectorException
    {
        return new HashMap<>() {{
            put("Memory Max", Runtime.getRuntime().maxMemory());
            put("Memory Total", Runtime.getRuntime().totalMemory());
            put("Memory free", Runtime.getRuntime().freeMemory());
            put("Memory used", Runtime.getRuntime().maxMemory() - Runtime.getRuntime().freeMemory());
        }};
    }
}
