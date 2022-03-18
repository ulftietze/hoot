package hoot.model.queue.publisher;

import hoot.system.objects.Inject;
import hoot.system.objects.ObjectManager;
import hoot.system.Queue.PublisherInterface;
import hoot.system.Queue.QueueManager;

public class HttpRequestDurationPublisher implements PublisherInterface
{
    public static final String QUEUE_ID = "http-request-duration";

    @Inject
    private QueueManager queueManager;

    @Override
    public void publish(Object queueData)
    {
        this.queueManager.add(QUEUE_ID, queueData);
    }
}
