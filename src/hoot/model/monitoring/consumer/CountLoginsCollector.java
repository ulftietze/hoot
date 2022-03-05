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
import java.util.ArrayList;
import java.util.Collections;
import java.util.NavigableMap;
import java.util.TreeMap;

public class CountLoginsCollector extends Thread implements CollectorInterface, ConsumerInterface
{
    public final static  String COLLECTOR_NAME                     = "CountLogins";
    private final static long   PERIOD_LOGINS_SINCE_HOURS          = 2;
    private final static long   PERIOD_CURRENTLY_LOGGED_IN_MINUTES = 1;

    private final QueueManager queueManager;

    private final NavigableMap<Instant, ArrayList<User>> loginsInPeriod;

    private final NavigableMap<Instant, ArrayList<User>> curLoggedIn;

    private final LoggerInterface logger;

    public CountLoginsCollector()
    {
        this.queueManager   = (QueueManager) ObjectManager.get(QueueManager.class);
        this.loginsInPeriod = Collections.synchronizedNavigableMap(new TreeMap<>());
        this.curLoggedIn    = Collections.synchronizedNavigableMap(new TreeMap<>());
        this.logger         = (LoggerInterface) ObjectManager.get(LoggerInterface.class);
    }

    @Override
    public void run()
    {
        while (true) {
            User    loggedInUser = (User) this.queueManager.take(LoginPublisher.QUEUE_ID);
            Instant now          = Instant.now();

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
        this.curLoggedIn.headMap(ofMinutes, true).clear();

        Instant ofHours = Instant.now().minus(Duration.ofHours(PERIOD_LOGINS_SINCE_HOURS));
        this.loginsInPeriod.headMap(ofHours, true).clear();

        return new CollectorResult()
        {{
            put("LoginsPerPeriod", loginsInPeriod.size());
            put("CurrentlyLoggedIn", curLoggedIn.size());
            put("CurrentlyLoggedInUsers", curLoggedIn);
        }};
    }
}
