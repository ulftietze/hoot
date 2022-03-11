package hoot.model.monitoring;

import hoot.system.Exception.CollectorException;
import hoot.system.Monitoring.CollectorInterface;
import hoot.system.Monitoring.CollectorResult;
import hoot.system.ObjectManager.ObjectManager;
import hoot.system.Queue.QueueManager;

public class QueueSizeCollector extends Thread implements CollectorInterface
{
    public final static String COLLECTOR_NAME = "MessageQueue Sizes";

    private final QueueManager queueManager;

    public QueueSizeCollector()
    {
        this.queueManager = (QueueManager) ObjectManager.get(QueueManager.class);
    }

    @Override
    public String getCollectorName()
    {
        return COLLECTOR_NAME;
    }

    @Override
    public CollectorResult collect() throws CollectorException
    {
        return new CollectorResult(this.queueManager.getQueueSizes());
    }
}
