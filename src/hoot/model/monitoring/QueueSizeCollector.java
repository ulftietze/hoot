package hoot.model.monitoring;

import hoot.system.Exception.CollectorException;
import hoot.system.Monitoring.CollectorInterface;
import hoot.system.Monitoring.CollectorResult;
import hoot.system.objects.Inject;
import hoot.system.objects.ObjectManager;
import hoot.system.Queue.QueueManager;

public class QueueSizeCollector extends Thread implements CollectorInterface
{
    public final static String COLLECTOR_NAME = "MessageQueue Sizes";

    @Inject private QueueManager queueManager;

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
