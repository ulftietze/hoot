package hoot.system.Queue;

import hoot.system.Logger.LoggerInterface;
import hoot.system.ObjectManager.ObjectManager;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class QueueManager
{
    private final Map<String, Queue> queues = new HashMap<>();

    public QueueManager()
    {
        LoggerInterface logger = (LoggerInterface) ObjectManager.get(LoggerInterface.class);
    }

    public void add(String queueName, Object data)
    {
        this.queues.computeIfAbsent(queueName, k -> (Queue) ObjectManager.create(Queue.class));
        this.queues.get(queueName).add(data);
    }

    public Object take(String queueName)
    {
        this.queues.computeIfAbsent(queueName, k -> (Queue) ObjectManager.create(Queue.class));
        return this.queues.get(queueName).take();
    }

    private class Queue
    {
        private final static int                QUEUE_SIZE = 20_000;
        private final        LinkedList<Object> queue      = new LinkedList<>();
        private final        LoggerInterface    logger;

        public Queue()
        {
            this.logger = (LoggerInterface) ObjectManager.get(LoggerInterface.class);
        }

        public synchronized void add(Object data)
        {
            while (QUEUE_SIZE <= this.queue.size()) {
                this.doWait();
            }

            this.queue.addLast(data);
            notifyAll();
        }

        public synchronized Object take()
        {
            while (this.queue.isEmpty()) {
                this.doWait();
            }

            Object entry = this.queue.remove();
            notifyAll();

            return entry;
        }

        private synchronized void doWait()
        {
            try {
                wait();
            } catch (InterruptedException e) {
                this.logger.log("[ERROR] Queue Interrupt while waiting: " + e.getMessage());
            }
        }
    }
}
