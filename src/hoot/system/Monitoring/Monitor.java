package hoot.system.Monitoring;

import hoot.system.Exception.CollectorException;
import hoot.system.Logger.LoggerInterface;
import hoot.system.ObjectManager.ObjectManager;

import java.util.ArrayList;

public class Monitor extends Thread
{
    private final static long ONE_SEC = 1_000_000_000;

    private final LoggerInterface               logger;
    private final MonitorData                   monitorData;
    private final ArrayList<CollectorInterface> collectorList;

    public Monitor()
    {
        this.collectorList = new ArrayList<>();
        this.monitorData   = (MonitorData) ObjectManager.create(MonitorData.class);
        this.logger        = (LoggerInterface) ObjectManager.get(LoggerInterface.class);
    }

    public MonitorData getMonitorData()
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

        while (true) {
            long now = System.nanoTime();

            // This may be flexibel and configurable
            if (now - last < ONE_SEC) {
                continue;
            }

            for (CollectorInterface collector : this.collectorList) {
                String collectorName = collector.getCollectorName();
                try {
                    this.monitorData.put(collectorName, collector.collect());
                } catch (CollectorException e) {
                    this.logger.log("[ERROR] Could not collect data from " + collectorName + ": " + e.getMessage());
                }
            }

            last = now;
        }
    }
}
