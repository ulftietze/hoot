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
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class CountRegistrationsCollector extends Thread implements CollectorInterface, ConsumerInterface
{
    public final static  String COLLECTOR_NAME               = "CountRegistrations";
    private final static long   PERIOD_REGISTRATIONS_MINUTES = 2;

    private final QueueManager queueManager;

    private final NavigableMap<Instant, ArrayList<User>> registrationsInPeriod;

    private final NavigableMap<Integer, Instant> userRegisteredInPeriod;

    private boolean running = true;

    private AtomicInteger currentlyRegisteredUsers;

    public CountRegistrationsCollector()
    {
        UserRepository userRepository = (UserRepository) ObjectManager.get(UserRepository.class);

        this.queueManager             = (QueueManager) ObjectManager.get(QueueManager.class);
        this.registrationsInPeriod    = Collections.synchronizedNavigableMap(new TreeMap<>());
        this.userRegisteredInPeriod   = Collections.synchronizedNavigableMap(new TreeMap<>());
        this.currentlyRegisteredUsers = new AtomicInteger(userRepository.getAllUsersCount());
    }

    @Override
    public void run()
    {
        ScheduledFuture<?> cleanUpScheduleTask = this
                .createScheduledExecutorService()
                .scheduleAtFixedRate(this::cleanup, 1, 1, TimeUnit.SECONDS);

        while (this.running) {
            User    registeredUser = (User) this.queueManager.take(RegistrationPublisher.QUEUE_ID);
            Instant now            = Instant.now();

            if (registeredUser == null) {
                continue;
            }

            this.userRegisteredInPeriod.computeIfPresent(registeredUser.id, (k, v) -> {
                this.registrationsInPeriod.get(v).removeIf(user -> Objects.equals(user.id, k));
                return now;
            });
            this.userRegisteredInPeriod.computeIfAbsent(registeredUser.id, k -> now);

            this.registrationsInPeriod.computeIfAbsent(now, k -> new ArrayList<>());
            this.registrationsInPeriod.get(now).add(registeredUser);
            this.currentlyRegisteredUsers.incrementAndGet();
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
            put("Currently Registered User", currentlyRegisteredUsers);
            put("Registrations in Period", userRegisteredInPeriod.size());
        }};
    }

    @Override
    public void stopRun()
    {
        this.running = false;
    }

    private void cleanup()
    {
        Instant ofMinutes = Instant.now().minus(Duration.ofHours(PERIOD_REGISTRATIONS_MINUTES));
        this.registrationsInPeriod.headMap(ofMinutes, false).clear();
    }

    private ScheduledExecutorService createScheduledExecutorService()
    {
        return (ScheduledExecutorService) ObjectManager.create(ScheduledExecutorService.class);
    }
}
