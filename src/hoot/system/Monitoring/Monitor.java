package hoot.system.Monitoring;

import hoot.system.Exception.CollectorException;
import hoot.system.Logger.LoggerInterface;
import hoot.system.objects.Inject;

import java.util.ArrayList;

public class Monitor extends Thread
{
    private final static long ONE_SEC = 1_000_000_000;

    private final ArrayList<CollectorInterface> collectorList;

    private boolean running = true;

    @Inject private LoggerInterface logger;

    @Inject(create = true) private MonitorData monitorData;

    public Monitor()
    {
        this.collectorList = new ArrayList<>();
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
        while (this.running) {
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

            try {
                // This may be flexibel and configurable
                Thread.sleep(1000);
            } catch (InterruptedException ignore) {
            }
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
