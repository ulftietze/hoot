package hoot.model.monitoring.consumer;

import hoot.model.entities.User;
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
    public final static  String COLLECTOR_NAME               = "Count Registrations";
    private final static long   PERIOD_REGISTRATIONS_MINUTES = 2;

    private final QueueManager queueManager;

    private final NavigableMap<Instant, ArrayList<User>> registrationsPerPeriod;

    private Integer currentlyRegisteredUsers;

    public CountRegistrationsCollector()
    {
        UserRepository userRepository = (UserRepository) ObjectManager.get(UserRepository.class);

        this.queueManager             = (QueueManager) ObjectManager.get(QueueManager.class);
        this.registrationsPerPeriod   = Collections.synchronizedNavigableMap(new TreeMap<>());
        this.currentlyRegisteredUsers = userRepository.getAllUsersCount();
    }

    @Override
    public void run()
    {
        while (true) {
            User    registeredUser = (User) this.queueManager.take(RegistrationPublisher.QUEUE_ID);
            Instant now            = Instant.now();

            this.registrationsPerPeriod.computeIfAbsent(now, k -> new ArrayList<>());
            this.registrationsPerPeriod.get(now).add(registeredUser);
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
        Instant ofMinutes = Instant.now().minus(Duration.ofHours(PERIOD_REGISTRATIONS_MINUTES));
        this.registrationsPerPeriod
                .headMap(ofMinutes, false)
                .forEach((instant, list) -> this.registrationsPerPeriod.remove(instant));

        return new HashMap<>()
        {{
            put("CurrentlyRegisteredUser", currentlyRegisteredUsers);
            put("RegistrationsPerPeriod", registrationsPerPeriod.size());
        }};
    }
}
