package hoot.model.monitoring.consumer;

import hoot.model.queue.publisher.HttpRequestDurationPublisher;
import hoot.system.Exception.CollectorException;
import hoot.system.Logger.LoggerInterface;
import hoot.system.Monitoring.CollectorInterface;
import hoot.system.Monitoring.CollectorResult;
import hoot.system.ObjectManager.ObjectManager;
import hoot.system.Queue.ConsumerInterface;
import hoot.system.Queue.QueueManager;
import hoot.system.util.Pair;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class RequestDurationCollector extends Thread implements CollectorInterface, ConsumerInterface
{
    public final static String COLLECTOR_NAME = "Request Duration Collector";
    public final static String ALL            = "All";

    private final QueueManager    queueManager;
    private final LoggerInterface logger;

    private final Map<String, List<Duration>> methodDurations;
    private final Map<String, Duration>       methodAverageDuration;

    private boolean running = true;

    public RequestDurationCollector()
    {
        this.queueManager = (QueueManager) ObjectManager.get(QueueManager.class);
        this.logger       = (LoggerInterface) ObjectManager.get(LoggerInterface.class);

        this.methodDurations       = Collections.synchronizedNavigableMap(new TreeMap<>());
        this.methodAverageDuration = Collections.synchronizedNavigableMap(new TreeMap<>());
    }

    @Override
    public void run()
    {
        ScheduledFuture<?> cleanUpScheduleTask = this
                .createScheduledExecutorService()
                .scheduleAtFixedRate(this::cleanup, 1, 1, TimeUnit.SECONDS);

        while (this.running) {
            try {
                var duration = (Pair<String, Duration>) this.queueManager.take(HttpRequestDurationPublisher.QUEUE_ID);

                if (duration == null) {
                    continue;
                }

                synchronized (this.methodDurations) {
                    this.methodDurations.computeIfAbsent(duration.first, k -> new ArrayList<>());
                    this.methodDurations.get(duration.first).add(duration.second);
                }
            } catch (Throwable e) {
                this.logger.logException("Something really weird happened, and this crashed: " + e.getMessage(), e);
            }
        }

        cleanUpScheduleTask.cancel(true);
    }

    @Override
    public String getCollectorName()
    {
        return COLLECTOR_NAME;
    }

    @Override
    public CollectorResult collect() throws CollectorException
    {
        return new CollectorResult(this.methodAverageDuration);
    }

    @Override
    public void stopRun()
    {
        this.running = false;
    }

    private void cleanup()
    {
        try {
            Duration avg      = Duration.ZERO;
            int      requests = 0;

            for (Map.Entry<String, List<Duration>> entry : this.methodDurations.entrySet()) {
                String         method    = entry.getKey();
                List<Duration> durations = entry.getValue();
                Duration       methodAvg = Duration.ZERO;

                for (Duration d : durations) {
                    methodAvg = methodAvg.plus(d);
                    requests++;
                }

                avg = avg.plus(methodAvg);

                int amount = !entry.getValue().isEmpty() ? entry.getValue().size() : 1;
                this.methodAverageDuration.put(method, methodAvg.dividedBy(amount));
            }

            this.methodAverageDuration.put(ALL, avg.dividedBy(requests != 0 ? requests : 1));

            synchronized (this.methodDurations) {
                this.methodDurations.clear();
            }

        } catch (Throwable t) {
            this.logger.logException("RequestDurationCollector::cleanup failed: " + t.getMessage(), t);
        }
    }

    private ScheduledExecutorService createScheduledExecutorService()
    {
        return (ScheduledExecutorService) ObjectManager.create(ScheduledExecutorService.class);
    }
}
