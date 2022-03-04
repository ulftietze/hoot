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

    private final List<Instant> loginsPerPeriod;

    private final Map<Instant, User> currentlyLoggedIn;

    public CountLoginsCollector()
    {
        this.queueManager      = (QueueManager) ObjectManager.get(QueueManager.class);
        this.loginsPerPeriod   = Collections.synchronizedList(new LinkedList<>());
        this.currentlyLoggedIn = Collections.synchronizedMap(new LinkedHashMap<>());
    }

    @Override
    public void run()
    {
        while (true) {
            User loggedIn = (User) this.queueManager.take(LoginPublisher.QUEUE_ID);
            this.loginsPerPeriod.add(Instant.now());
            this.currentlyLoggedIn.put(Instant.now(), loggedIn);
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
        // TODO: HashMap for calculation is better than linked list!

        Instant now = Instant.now();

        Iterator<Map.Entry<Instant, User>> it = this.currentlyLoggedIn.entrySet().iterator();
        while (it.hasNext()) {
            Instant from = it.next().getKey();

            if (Duration.between(from, now).compareTo(Duration.ofMinutes(PERIOD_CURRENTLY_LOGGED_IN_MINUTES)) <= 0) {
                break;
            }

            it.remove();
        }

        ListIterator<Instant> loginsPerPeriodIt = this.loginsPerPeriod.listIterator();
        while (loginsPerPeriodIt.hasNext()) {
            Instant t = loginsPerPeriodIt.next();

            if (Duration.between(t, now).compareTo(Duration.ofHours(PERIOD_LOGINS_SINCE_HOURS)) <= 0) {
                break;
            }

            loginsPerPeriodIt.remove();
        }

        return new HashMap<>()
        {{
            put("LoginsPerPeriod", loginsPerPeriod.size());
            put("CurrentlyLoggedIn", currentlyLoggedIn.size());
            put("CurrentlyLoggedInUsers", currentlyLoggedIn);
        }};
    }
}
