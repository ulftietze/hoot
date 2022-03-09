package hoot.system.Queue;

import hoot.system.Logger.LoggerInterface;
import hoot.system.ObjectManager.ObjectManager;
import hoot.system.Queue.concurrent.LinkedBlockingQueue;

import java.util.Collections;
import java.util.NavigableMap;
import java.util.TreeMap;

public class QueueManager
{
    private final NavigableMap<String, LinkedBlockingQueue<Object>> queues;

    private final LoggerInterface logger;

    public QueueManager()
    {
        this.queues = Collections.synchronizedNavigableMap(new TreeMap<>());
        this.logger = (LoggerInterface) ObjectManager.get(LoggerInterface.class);
    }

    public void add(String queueName, Object data)
    {
        this.queues.computeIfAbsent(queueName, k -> new LinkedBlockingQueue<>(20000));

        try {
            this.queues.get(queueName).put(data);
        } catch (InterruptedException e) {
            this.logger.logException("Could not put to Queue: " + e.getMessage(), e);
        }
    }

    public Object take(String queueName)
    {
        this.queues.computeIfAbsent(queueName, k -> new LinkedBlockingQueue<>(20000));

        try {
            return this.queues.get(queueName).take();
        } catch (InterruptedException e) {
            this.logger.log(e.getMessage());
            return null;
        }
    }
}
