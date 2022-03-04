package hoot.model.monitoring.consumer;

import hoot.model.entities.User;
import hoot.model.queue.publisher.LoginPublisher;
import hoot.system.Exception.CollectorException;
import hoot.system.Monitoring.CollectorInterface;
import hoot.system.ObjectManager.ObjectManager;
import hoot.system.Queue.ConsumerInterface;
import hoot.system.Queue.QueueManager;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class CountLoginsCollector extends Thread implements CollectorInterface, ConsumerInterface
{
    public final static  String COLLECTOR_NAME                     = "Count Logins";
    private final static long   PERIOD_LOGINS_SINCE_HOURS          = 2;
    private final static long   PERIOD_CURRENTLY_LOGGED_IN_MINUTES = 10;

    private final QueueManager queueManager;

    private final NavigableMap<Instant, ArrayList<User>> loginsPerPeriod;

    private final NavigableMap<Instant, ArrayList<User>> currentlyLoggedIn;

    public CountLoginsCollector()
    {
        this.queueManager      = (QueueManager) ObjectManager.get(QueueManager.class);
        this.loginsPerPeriod   = Collections.synchronizedNavigableMap(new TreeMap<>());
        this.currentlyLoggedIn = Collections.synchronizedNavigableMap(new TreeMap<>());
    }

    @Override
    public void run()
    {
        while (true) {
            User    loggedInUser = (User) this.queueManager.take(LoginPublisher.QUEUE_ID);
            Instant now          = Instant.now();

            this.loginsPerPeriod.computeIfAbsent(now, k -> new ArrayList<>());
            this.loginsPerPeriod.get(now).add(loggedInUser);
            this.currentlyLoggedIn.computeIfAbsent(now, k -> new ArrayList<>());
            this.currentlyLoggedIn.get(now).add(loggedInUser);
        }
    }

    @Override
    public String getCollectorName()
    {
        return COLLECTOR_NAME;
    }

    @Override
    public Object collect() throws CollectorException
    {
        Instant ofMinutes = Instant.now().minus(Duration.ofMinutes(PERIOD_CURRENTLY_LOGGED_IN_MINUTES));
        this.currentlyLoggedIn
                .headMap(ofMinutes, false)
                .forEach((instant, list) -> this.currentlyLoggedIn.remove(instant));

        Instant ofHours = Instant.now().minus(Duration.ofHours(PERIOD_LOGINS_SINCE_HOURS));
        this.loginsPerPeriod
                .headMap(ofMinutes, false)
                .forEach((instant, list) -> this.loginsPerPeriod.remove(instant));

        return new HashMap<>()
        {{
            put("LoginsPerPeriod", loginsPerPeriod.size());
            put("CurrentlyLoggedIn", currentlyLoggedIn.size());
            put("CurrentlyLoggedInUsers", currentlyLoggedIn);
        }};
    }
}
