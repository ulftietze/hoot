package hoot.model.monitoring;

import com.sun.management.OperatingSystemMXBean;
import hoot.system.Exception.CollectorException;
import hoot.system.Logger.LoggerInterface;
import hoot.system.Monitoring.CollectorInterface;
import hoot.system.Monitoring.CollectorResult;
import hoot.system.ObjectManager.ObjectManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SystemWorkloadCollector extends Thread implements CollectorInterface
{
    public final static String COLLECTOR_NAME = "System Workload";

    public final static String MEMORY_CLASS_RESERVED   = "Class Reserved";
    public final static String MEMORY_CLASS_COMMITTED  = "Class Committed";
    public final static String MEMORY_CLASS_CLASSES    = "Class Classes";
    public final static String MEMORY_THREAD_RESERVED  = "Thread Reserved";
    public final static String MEMORY_THREAD_COMMITTED = "Thread Committed";
    public final static String MEMORY_CODE_RESERVED    = "Code Reserved";
    public final static String MEMORY_CODE_COMMITTED   = "Code Committed";
    public final static String MEMORY_GC_RESERVED      = "GC Reserved";
    public final static String MEMORY_GC_COMMITTED     = "GC Committed";

    private final OperatingSystemMXBean system;

    private final ThreadMXBean thread;

    private final LoggerInterface logger;

    private final Map<String, Integer> memoryInfo;

    private boolean running = true;

    public SystemWorkloadCollector()
    {
        this.memoryInfo = Collections.synchronizedMap(new HashMap<>());
        this.system     = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        this.thread     = ManagementFactory.getThreadMXBean();
        this.logger     = (LoggerInterface) ObjectManager.get(LoggerInterface.class);
    }

    @Override
    public void run()
    {
        while (this.running) {
            try {
                this.updateMemoryInfo();
            } catch (Throwable e) {
                this.logger.logException("Something really weird happened, and this crashed: " + e.getMessage(), e);
            }

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException ignore) {
            }
        }
    }

    @Override
    public String getCollectorName()
    {
        return COLLECTOR_NAME;
    }

    @Override
    public synchronized CollectorResult collect() throws CollectorException
    {
        return new CollectorResult()
        {{
            put("Available Processors", system.getAvailableProcessors());
            put("System Load Average", system.getSystemLoadAverage());
            put("System CPU Load", system.getSystemCpuLoad());
            put("Process CPU Load", system.getProcessCpuLoad());
            put("Thread Count", thread.getThreadCount());
            put("Thread Total Started Count", thread.getTotalStartedThreadCount());
            putAll(memoryInfo);
        }};
    }

    public void stopRun()
    {
        this.running = false;
    }

    public void updateMemoryInfo()
    {
        ProcessBuilder processBuilder = (ProcessBuilder) ObjectManager.create(ProcessBuilder.class);
        processBuilder.command("/bin/bash", "-c", "jcmd $(cat /my/tomcat/pid) VM.native_memory scale=MB");

        try {
            Process process = processBuilder.start();

            BufferedReader outputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            HashMap<String, Integer> newVariableStorage = new HashMap<>();

            String line;
            while ((line = outputReader.readLine()) != null) {
                if (line.contains("Class (")) {
                    int[] res = getReservedAndCommitted(line);
                    this.memoryInfo.put(MEMORY_CLASS_RESERVED, res[0]);
                    this.memoryInfo.put(MEMORY_CLASS_COMMITTED, res[1]);
                } else if (line.contains("(classes")) {
                    String tmp = line.split("#")[1].stripTrailing();
                    tmp = tmp.substring(0, tmp.length() - 1);
                    try {
                        this.memoryInfo.put(MEMORY_CLASS_CLASSES, Integer.parseInt(tmp));
                    } catch (NumberFormatException ignore) {
                    }
                } else if (line.contains("Thread")) {
                    int[] res = getReservedAndCommitted(line);
                    this.memoryInfo.put(MEMORY_THREAD_RESERVED, res[0]);
                    this.memoryInfo.put(MEMORY_THREAD_COMMITTED, res[1]);
                } else if (line.contains("Code")) {
                    int[] res = getReservedAndCommitted(line);
                    this.memoryInfo.put(MEMORY_CODE_RESERVED, res[0]);
                    this.memoryInfo.put(MEMORY_CODE_COMMITTED, res[1]);
                } else if (line.contains("GC")) {
                    int[] res = getReservedAndCommitted(line);
                    this.memoryInfo.put(MEMORY_GC_RESERVED, res[0]);
                    this.memoryInfo.put(MEMORY_GC_COMMITTED, res[1]);
                }
            }
        } catch (IOException e) {
            this.logger.log("JCMD process could not be run: " + e.getMessage());
        }
    }

    private int[] getReservedAndCommitted(String line)
    {
        int[] values = new int[2];
        try {
            values[0] = Integer.parseInt(line.split("reserved=")[1].split("MB,")[0]);
            values[1] = Integer.parseInt(line.split("committed=")[1].split("MB")[0]);
        } catch (NumberFormatException ignore) {
        }

        return values;
    }
}
