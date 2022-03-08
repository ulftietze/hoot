package hoot.model.monitoring;

import com.sun.management.OperatingSystemMXBean;
import hoot.system.Exception.CollectorException;
import hoot.system.Monitoring.CollectorInterface;
import hoot.system.Monitoring.CollectorResult;

import java.lang.management.ManagementFactory;

public class SystemWorkloadCollector implements CollectorInterface
{
    public final static String COLLECTOR_NAME = "System Workload";

    OperatingSystemMXBean system;

    public SystemWorkloadCollector()
    {
        this.system = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    }

    @Override
    public String getCollectorName()
    {
        return COLLECTOR_NAME;
    }

    @Override
    public CollectorResult collect() throws CollectorException
    {
        return new CollectorResult()
        {{
            put("Available Processors", system.getAvailableProcessors());
            put("System Load Average", system.getSystemLoadAverage());
            put("System CPU Load", system.getSystemCpuLoad());
            put("Process CPU Load", system.getSystemCpuLoad());
            put("Memory Max", Runtime.getRuntime().maxMemory());
            put("Memory Total", Runtime.getRuntime().totalMemory());
            put("Memory free", Runtime.getRuntime().freeMemory());
            put("Memory used", Runtime.getRuntime().maxMemory() - Runtime.getRuntime().freeMemory());
        }};
    }
}
