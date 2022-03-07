package hoot.system.Monitoring;

import hoot.system.Exception.CollectorException;
import hoot.system.Logger.LoggerInterface;
import hoot.system.ObjectManager.ObjectManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Monitor extends Thread
{
    private final static long ONE_SEC = 1_000_000_000;

    private final LoggerInterface               logger;
    private final MonitorData                   monitorData;
    private final ArrayList<CollectorInterface> collectorList;
    private       boolean                       running = true;

    public Monitor()
    {
        this.collectorList = new ArrayList<>();
        this.monitorData   = (MonitorData) ObjectManager.create(MonitorData.class);
        this.logger        = (LoggerInterface) ObjectManager.get(LoggerInterface.class);
    }

    public synchronized MonitorData getMonitorData()
    {
        return this.monitorData;
    }

    public void addCollector(CollectorInterface collector)
    {
        this.collectorList.add(collector);
    }

    @Override
    public void run()
    {
        long last = System.nanoTime();

        while (this.running) {
            long now = System.nanoTime();

            // This may be flexibel and configurable
            if (now - last < ONE_SEC) {
                continue;
            }

            for (CollectorInterface collector : this.collectorList) {
                String collectorName = collector.getCollectorName();
                try {
                    this.putToMonitorData(collectorName, collector.collect());
                } catch (CollectorException e) {
                    this.logger.log("[ERROR] Could not collect data from " + collectorName + ": " + e.getMessage());
                } catch (Exception e) {
                    String msg = "[ERROR] Could not collect data: " + e.getClass().getName() + "=>" + e.getMessage();
                    this.logger.logException(msg, e);
                }
            }

            last = now;
        }
    }

    private synchronized void putToMonitorData(String collectorName, CollectorResult result)
    {
        this.monitorData.put(collectorName, result);
    }

    public void stopRun()
    {
        this.running = false;
    }
}
