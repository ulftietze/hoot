package hoot.model.monitoring.consumer;

import hoot.model.entities.User;
import hoot.model.queue.publisher.RegistrationPublisher;
import hoot.model.repositories.UserRepository;
import hoot.system.Exception.CollectorException;
import hoot.system.Monitoring.CollectorInterface;
import hoot.system.Monitoring.CollectorResult;
import hoot.system.ObjectManager.ObjectManager;
import hoot.system.Queue.ConsumerInterface;
import hoot.system.Queue.QueueManager;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class CountRegistrationsCollector extends Thread implements CollectorInterface, ConsumerInterface
{
    public final static  String COLLECTOR_NAME               = "CountRegistrations";
    private final static long   PERIOD_REGISTRATIONS_MINUTES = 2;

    private final QueueManager queueManager;

    private final NavigableMap<Instant, ArrayList<User>> registrationsInPeriod;

    private final NavigableMap<Integer, Instant> userRegisteredInPeriod;

    private Integer currentlyRegisteredUsers;

    public CountRegistrationsCollector()
    {
        UserRepository userRepository = (UserRepository) ObjectManager.get(UserRepository.class);

        this.queueManager             = (QueueManager) ObjectManager.get(QueueManager.class);
        this.registrationsInPeriod    = Collections.synchronizedNavigableMap(new TreeMap<>());
        this.userRegisteredInPeriod   = Collections.synchronizedNavigableMap(new TreeMap<>());
        this.currentlyRegisteredUsers = userRepository.getAllUsersCount();
    }

    @Override
    public void run()
    {
        while (true) {
            User    registeredUser = (User) this.queueManager.take(RegistrationPublisher.QUEUE_ID);
            Instant now            = Instant.now();

            this.userRegisteredInPeriod.computeIfPresent(registeredUser.id, (k, v) -> {
                this.registrationsInPeriod.get(v).removeIf(user -> Objects.equals(user.id, k));
                return now;
            });
            this.userRegisteredInPeriod.computeIfAbsent(registeredUser.id, k -> now);

            this.registrationsInPeriod.computeIfAbsent(now, k -> new ArrayList<>());
            this.registrationsInPeriod.get(now).add(registeredUser);
            this.currentlyRegisteredUsers++;
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
        Instant ofMinutes = Instant.now().minus(Duration.ofHours(PERIOD_REGISTRATIONS_MINUTES));
        this.registrationsInPeriod.headMap(ofMinutes, false).clear();

        return new CollectorResult()
        {{
            put("CurrentlyRegisteredUser", currentlyRegisteredUsers);
            put("RegistrationsPerPeriod", userRegisteredInPeriod.size());
        }};
    }
}
