package hoot.model.queue.publisher;

import hoot.system.ObjectManager.ObjectManager;
import hoot.system.Queue.PublisherInterface;
import hoot.system.Queue.QueueManager;

public class RegistrationPublisher implements PublisherInterface
{
    public static final String QUEUE_ID = "registration";

    private final QueueManager queueManager;

    public RegistrationPublisher()
    {
        this.queueManager = (QueueManager) ObjectManager.get(QueueManager.class);
    }

    @Override
    public void publish(Object queueData)
    {
        new Thread(() -> this.queueManager.add(QUEUE_ID, queueData)).start();
    }
}
