package hoot.model.queue.publisher;

import hoot.system.ObjectManager.ObjectManager;
import hoot.system.Queue.PublisherInterface;
import hoot.system.Queue.QueueManager;

public class TagsPublisher implements PublisherInterface
{
    public static final String QUEUE_ID = "tags";

    private final QueueManager queueManager;

    public TagsPublisher()
    {
        this.queueManager = (QueueManager) ObjectManager.get(QueueManager.class);
    }

    @Override
    public void publish(Object queueData)
    {
        this.queueManager.add(QUEUE_ID, queueData);
    }
}
