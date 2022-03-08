package hoot.model.queue.publisher;

import hoot.system.ObjectManager.ObjectManager;
import hoot.system.Queue.PublisherInterface;
import hoot.system.Queue.QueueManager;

public class HttpRequestPublisher implements PublisherInterface
{
    public static final String QUEUE_ID = "http-request";

    private final QueueManager queueManager;

    public HttpRequestPublisher()
    {
        this.queueManager = (QueueManager) ObjectManager.get(QueueManager.class);
    }

    @Override
    public void publish(Object queueData)
    {
        this.queueManager.add(QUEUE_ID, queueData);
    }
}