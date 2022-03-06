package hoot.model.monitoring.consumer;

import hoot.model.entities.User;
import hoot.model.queue.publisher.LoginPublisher;
import hoot.system.Exception.CollectorException;
import hoot.system.Logger.LoggerInterface;
import hoot.system.Monitoring.CollectorInterface;
import hoot.system.Monitoring.CollectorResult;
import hoot.system.ObjectManager.ObjectManager;
import hoot.system.Queue.ConsumerInterface;
import hoot.system.Queue.QueueManager;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class CountLoginsCollector extends Thread implements CollectorInterface, ConsumerInterface
{
    public final static  String COLLECTOR_NAME                     = "CountLogins";
    private final static long   PERIOD_LOGINS_SINCE_HOURS          = 2;
    private final static long   PERIOD_CURRENTLY_LOGGED_IN_MINUTES = 1;

    private final QueueManager                           queueManager;
    private final NavigableMap<Instant, ArrayList<User>> loginsInPeriod;
    private final NavigableMap<Integer, Instant>         userLoggedInInPeriod;
    private final NavigableMap<Instant, ArrayList<User>> curLoggedIn;
    private final NavigableMap<Integer, Instant>         userCurLoggedIn;
    private final LoggerInterface                        logger;
    private       boolean                                running = true;

    public CountLoginsCollector()
    {
        this.queueManager         = (QueueManager) ObjectManager.get(QueueManager.class);
        this.loginsInPeriod       = Collections.synchronizedNavigableMap(new TreeMap<>());
        this.userCurLoggedIn      = Collections.synchronizedNavigableMap(new TreeMap<>());
        this.userLoggedInInPeriod = Collections.synchronizedNavigableMap(new TreeMap<>());
        this.curLoggedIn          = Collections.synchronizedNavigableMap(new TreeMap<>());
        this.logger               = (LoggerInterface) ObjectManager.get(LoggerInterface.class);
    }

    @Override
    public void run()
    {
        while (this.running) {
            User    loggedInUser = (User) this.queueManager.take(LoginPublisher.QUEUE_ID);
            Instant now          = Instant.now();

            if (loggedInUser == null) {
                continue;
            }

            this.userCurLoggedIn.computeIfPresent(loggedInUser.id, (k, v) -> {
                this.curLoggedIn.get(v).removeIf(user -> Objects.equals(user.id, k));
                return now;
            });
            this.userCurLoggedIn.computeIfAbsent(loggedInUser.id, k -> now);

            this.userLoggedInInPeriod.computeIfPresent(loggedInUser.id, (k, v) -> {
                this.loginsInPeriod.get(v).removeIf(user -> Objects.equals(user.id, k));
                return now;
            });
            this.userLoggedInInPeriod.computeIfAbsent(loggedInUser.id, k -> now);

            this.loginsInPeriod.computeIfAbsent(now, k -> new ArrayList<>());
            this.loginsInPeriod.get(now).add(loggedInUser);
            this.curLoggedIn.computeIfAbsent(now, k -> new ArrayList<>());
            this.curLoggedIn.get(now).add(loggedInUser);
        }
    }

    @Override
    public String getCollectorName()
    {
        return COLLECTOR_NAME;
    }

    @Override
    public CollectorResult collect() throws CollectorException
    {
        Instant ofMinutes = Instant.now().minus(Duration.ofMinutes(PERIOD_CURRENTLY_LOGGED_IN_MINUTES));
        Instant ofHours   = Instant.now().minus(Duration.ofHours(PERIOD_LOGINS_SINCE_HOURS));

        Map<Instant, ArrayList<User>> currentlyLoggedIn = this.curLoggedIn.headMap(ofMinutes, true);
        currentlyLoggedIn.forEach((instant, users) -> users.forEach(user -> this.userCurLoggedIn.remove(user.id)));
        currentlyLoggedIn.clear();

        Map<Instant, ArrayList<User>> loginInPeriod = this.loginsInPeriod.headMap(ofHours, true);
        loginInPeriod.forEach((instant, users) -> users.forEach(user -> this.userLoggedInInPeriod.remove(user.id)));
        loginInPeriod.clear();

        return new CollectorResult()
        {{
            put("LoginsPerPeriod", userLoggedInInPeriod.size());
            put("CurrentlyLoggedIn", userCurLoggedIn.size());
            put("CurrentlyLoggedInUsers", curLoggedIn);
        }};
    }

    @Override
    public void stopRun()
    {
        this.running = false;
    }
}
