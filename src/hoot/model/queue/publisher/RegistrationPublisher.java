package hoot.model.queue.publisher;

import hoot.system.objects.Inject;
import hoot.system.objects.ObjectManager;
import hoot.system.Queue.PublisherInterface;
import hoot.system.Queue.QueueManager;

public class RegistrationPublisher implements PublisherInterface
{
    public static final String QUEUE_ID = "registration";

    @Inject private QueueManager queueManager;

    @Override
    public void publish(Object queueData)
    {
        this.queueManager.add(QUEUE_ID, queueData);
    }
}
