package hoot.model.monitoring.consumer;

import hoot.model.entities.User;
import hoot.model.queue.publisher.LoginPublisher;
import hoot.system.Exception.CollectorException;
import hoot.system.Logger.LoggerInterface;
import hoot.system.Monitoring.CollectorInterface;
import hoot.system.Monitoring.CollectorResult;
import hoot.system.Queue.ConsumerInterface;
import hoot.system.Queue.QueueManager;
import hoot.system.objects.Inject;
import hoot.system.objects.ObjectManager;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class CountLoginsCollector extends Thread implements CollectorInterface, ConsumerInterface
{
    public final static  String COLLECTOR_NAME            = "CountLogins";
    private final static long   PERIOD_LOGINS_SINCE_HOURS = 24;

    private final NavigableMap<Instant, ArrayList<User>> loginsInPeriod;
    private final NavigableMap<Integer, Instant>         userLoggedInInPeriod;

    private boolean running = true;

    @Inject private QueueManager    queueManager;
    @Inject private LoggerInterface logger;

    public CountLoginsCollector()
    {
        this.loginsInPeriod       = Collections.synchronizedNavigableMap(new TreeMap<>());
        this.userLoggedInInPeriod = Collections.synchronizedNavigableMap(new TreeMap<>());
    }

    @Override
    public void run()
    {
        ScheduledFuture<?> cleanUpScheduleTask = this
                .createScheduledExecutorService()
                .scheduleAtFixedRate(this::cleanup, 1, 1, TimeUnit.SECONDS);

        while (this.running) {
            try {
                User    loggedInUser = (User) this.queueManager.take(LoginPublisher.QUEUE_ID);
                Instant now          = Instant.now();

                if (loggedInUser == null) {
                    continue;
                }

                this.loginsInPeriod.computeIfAbsent(now, k -> new ArrayList<>());
                this.loginsInPeriod.get(now).add(loggedInUser);

                this.userLoggedInInPeriod.computeIfPresent(loggedInUser.id, (k, v) -> {
                    this.loginsInPeriod.get(v).removeIf(user -> Objects.equals(user.id, k));
                    return now;
                });
                this.userLoggedInInPeriod.computeIfAbsent(loggedInUser.id, k -> now);
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
        return new CollectorResult()
        {{
            put("LoginsPerPeriod", userLoggedInInPeriod.size());
        }};
    }

    @Override
    public void stopRun()
    {
        this.running = false;
    }

    private void cleanup()
    {
        Instant ofHours = Instant.now().minus(Duration.ofHours(PERIOD_LOGINS_SINCE_HOURS));

        Map<Instant, ArrayList<User>> loginInPeriod = this.loginsInPeriod.headMap(ofHours, true);
        loginInPeriod.forEach((instant, users) -> users.forEach(user -> this.userLoggedInInPeriod.remove(user.id)));
        loginInPeriod.clear();
    }

    private ScheduledExecutorService createScheduledExecutorService()
    {
        return (ScheduledExecutorService) ObjectManager.create(ScheduledExecutorService.class);
    }
}
