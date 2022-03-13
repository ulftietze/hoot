package hoot.model.monitoring.consumer;

import hoot.model.entities.User;
import hoot.model.queue.publisher.HttpRequestPublisher;
import hoot.model.repositories.UserRepository;
import hoot.system.Exception.CollectorException;
import hoot.system.Exception.EntityNotFoundException;
import hoot.system.Logger.LoggerInterface;
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

public class RequestsCollector extends Thread implements CollectorInterface, ConsumerInterface
{
    public final static  String COLLECTOR_NAME                     = "RequestsCollector";
    private final static long   PERIOD_CURRENTLY_LOGGED_IN_MINUTES = 10;

    private final QueueManager                           queueManager;
    private final NavigableMap<Instant, ArrayList<User>> curLoggedIn;
    private final NavigableMap<Integer, Instant>         userCurLoggedIn;
    private final AtomicInteger                          requestsCurrentSecond;
    private final AtomicInteger                          requestsLastSecond;
    private final UserRepository                         userRepository;
    private final LoggerInterface                        logger;

    private boolean running = true;

    public RequestsCollector()
    {
        this.queueManager = (QueueManager) ObjectManager.get(QueueManager.class);

        this.userCurLoggedIn       = Collections.synchronizedNavigableMap(new TreeMap<>());
        this.curLoggedIn           = Collections.synchronizedNavigableMap(new TreeMap<>());
        this.requestsLastSecond    = new AtomicInteger(0);
        this.requestsCurrentSecond = new AtomicInteger(0);

        this.userRepository = (UserRepository) ObjectManager.get(UserRepository.class);
        this.logger         = (LoggerInterface) ObjectManager.get(LoggerInterface.class);
    }

    @Override
    public void run()
    {
        ScheduledFuture<?> cleanUpScheduleTask = this
                .createScheduledExecutorService()
                .scheduleAtFixedRate(this::cleanup, 1, 1, TimeUnit.SECONDS);

        while (this.running) {
            try {
                var     request = (HashMap<String, Object>) this.queueManager.take(HttpRequestPublisher.QUEUE_ID);
                Instant now     = Instant.now();

                if (request == null) {
                    continue;
                }

                this.requestsCurrentSecond.incrementAndGet();

                Integer userId = (Integer) request.get("userId");

                if (userId != null) {
                    try {
                        User loggedInUser = this.userRepository.getById(userId);

                        this.userCurLoggedIn.computeIfPresent(loggedInUser.id, (id, timestamp) -> {
                            this.curLoggedIn.get(timestamp).removeIf(user -> Objects.equals(user.id, id));
                            return now;
                        });
                        this.userCurLoggedIn.computeIfAbsent(loggedInUser.id, k -> now);
                    } catch (EntityNotFoundException e) {
                        this.logger.logException("User " + userId + " from Session does not exist", e);
                    }
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
        return new CollectorResult()
        {{
            put("Currently Logged In", userCurLoggedIn.size());
            put("Currently Logged In Users", curLoggedIn);
            put("Requests Per Second", requestsLastSecond.get());
            // TODO: Collect Requests per Second logged in
        }};
    }

    @Override
    public void stopRun()
    {
        this.running = false;
    }

    private void cleanup()
    {
        this.requestsLastSecond.set(this.requestsCurrentSecond.getAndSet(0));

        Instant ofMinutes = Instant.now().minus(Duration.ofMinutes(PERIOD_CURRENTLY_LOGGED_IN_MINUTES));

        Map<Instant, ArrayList<User>> currentlyLoggedIn = this.curLoggedIn.headMap(ofMinutes, true);
        currentlyLoggedIn.forEach((instant, users) -> users.forEach(user -> this.userCurLoggedIn.remove(user.id)));
        currentlyLoggedIn.clear();
    }

    private ScheduledExecutorService createScheduledExecutorService()
    {
        return (ScheduledExecutorService) ObjectManager.create(ScheduledExecutorService.class);
    }
}
