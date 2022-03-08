package hoot.model.monitoring;

import com.sun.management.OperatingSystemMXBean;
import hoot.system.Exception.CollectorException;
import hoot.system.Monitoring.CollectorInterface;
import hoot.system.Monitoring.CollectorResult;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.ThreadMXBean;

public class SystemWorkloadCollector implements CollectorInterface
{
    public final static String COLLECTOR_NAME = "System Workload";

    private final MemoryMXBean memory;

    private final OperatingSystemMXBean system;

    private final ThreadMXBean thread;

    public SystemWorkloadCollector()
    {
        this.system = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        this.memory = ManagementFactory.getMemoryMXBean();
        this.thread = ManagementFactory.getThreadMXBean();
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
            put("Process CPU Load", system.getProcessCpuLoad());
            put("Memory Max", Runtime.getRuntime().maxMemory());
            put("Memory Total", Runtime.getRuntime().totalMemory());
            put("Memory Free", Runtime.getRuntime().freeMemory());
            put("Memory Used", Runtime.getRuntime().maxMemory() - Runtime.getRuntime().freeMemory());
            put("Memory Heap Usage", memory.getHeapMemoryUsage());
            put("Memory NonHeap Usage", memory.getNonHeapMemoryUsage());
            put("Thread Count", thread.getThreadCount());
            put("Thread Total Started Count", thread.getTotalStartedThreadCount());
        }};
    }
}
