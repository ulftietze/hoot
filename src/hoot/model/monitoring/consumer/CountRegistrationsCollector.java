package hoot.model.monitoring.consumer;

import hoot.model.queue.publisher.RegistrationPublisher;
import hoot.model.repositories.UserRepository;
import hoot.system.Exception.CollectorException;
import hoot.system.Monitoring.CollectorInterface;
import hoot.system.ObjectManager.ObjectManager;
import hoot.system.Queue.ConsumerInterface;
import hoot.system.Queue.QueueManager;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class CountRegistrationsCollector extends Thread implements CollectorInterface, ConsumerInterface
{
    public final static  String COLLECTOR_NAME            = "Count Registrations";
    private final static long   PERIOD_LOGINS_SINCE_HOURS = 2;

    private final QueueManager queueManager;

    private final List<Instant> registrationsPerPeriod;

    private Integer currentlyRegisteredUsers;

    public CountRegistrationsCollector()
    {
        UserRepository userRepository = (UserRepository) ObjectManager.get(UserRepository.class);

        this.queueManager            = (QueueManager) ObjectManager.get(QueueManager.class);
        this.registrationsPerPeriod   = Collections.synchronizedList(new LinkedList<>());
        this.currentlyRegisteredUsers = userRepository.getAllUsersCount();
    }

    @Override
    public void run()
    {
        while (true) {
            this.queueManager.take(RegistrationPublisher.QUEUE_ID);
            this.registrationsPerPeriod.add(Instant.now());
            this.currentlyRegisteredUsers++;
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
        Instant now = Instant.now();

        ListIterator<Instant> registrationsPerPeriodIt = this.registrationsPerPeriod.listIterator();
        while (registrationsPerPeriodIt.hasNext()) {
            Instant t = registrationsPerPeriodIt.next();

            if (Duration.between(t, now).compareTo(Duration.ofHours(PERIOD_LOGINS_SINCE_HOURS)) <= 0) {
                break;
            }

            registrationsPerPeriodIt.remove();
        }

        return new HashMap<>()
        {{
            put("CurrentlyRegisteredUser", currentlyRegisteredUsers);
            put("RegistrationsPerPeriod", registrationsPerPeriod.size());
        }};
    }
}
