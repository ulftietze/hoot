package hoot.model.monitoring.consumer;

import hoot.model.queue.publisher.HttpRequestDurationPublisher;
import hoot.system.Exception.CollectorException;
import hoot.system.Logger.LoggerInterface;
import hoot.system.Monitoring.CollectorInterface;
import hoot.system.Monitoring.CollectorResult;
import hoot.system.Queue.ConsumerInterface;
import hoot.system.Queue.QueueManager;
import hoot.system.objects.Inject;
import hoot.system.objects.ObjectManager;
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
    public final static String GET            = "GET";
    public final static String PUT            = "PUT";
    public final static String POST           = "POST";
    public final static String DELETE         = "DELETE";
    public final static String OPTION         = "OPTION";

    private final Map<String, List<Duration>> methodDurations;
    private final Map<String, Duration>       methodAverageDuration;

    private boolean running = true;

    @Inject private QueueManager    queueManager;
    @Inject private LoggerInterface logger;

    public RequestDurationCollector()
    {
        this.methodAverageDuration = Collections.synchronizedNavigableMap(new TreeMap<>());
        this.methodDurations       = Collections.synchronizedNavigableMap(new TreeMap<>());
        this.resetAverage();
    }

    private void resetAverage()
    {
        // TODO: More
        this.methodAverageDuration.put(ALL, Duration.ZERO);
        this.methodAverageDuration.put(GET, Duration.ZERO);
        this.methodAverageDuration.put(PUT, Duration.ZERO);
        this.methodAverageDuration.put(POST, Duration.ZERO);
        this.methodAverageDuration.put(DELETE, Duration.ZERO);
        this.methodAverageDuration.put(OPTION, Duration.ZERO);
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

            synchronized (this.methodDurations) {
                this.resetAverage();
                for (Map.Entry<String, List<Duration>> entry : this.methodDurations.entrySet()) {
                    String         method    = entry.getKey();
                    List<Duration> durations = entry.getValue();
                    Duration       methodAvg = Duration.ZERO;

                    for (Duration d : durations) {
                        methodAvg = methodAvg.plus(d);
                        requests++;
                    }

                    int amount = !entry.getValue().isEmpty() ? entry.getValue().size() : 1;
                    this.methodAverageDuration.put(method, methodAvg.dividedBy(amount));

                    avg = avg.plus(methodAvg);
                }

                this.methodAverageDuration.put(ALL, avg.dividedBy(requests != 0 ? requests : 1));
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
