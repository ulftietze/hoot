package hoot.model.monitoring.consumer;

import hoot.model.queue.publisher.RegistrationPublisher;
import hoot.model.repositories.UserRepository;
import hoot.model.search.UserSearchCriteria;
import hoot.system.Exception.CollectorException;
import hoot.system.Logger.LoggerInterface;
import hoot.system.Monitoring.CollectorInterface;
import hoot.system.Monitoring.CollectorResult;
import hoot.system.ObjectManager.ObjectManager;
import hoot.system.Queue.ConsumerInterface;
import hoot.system.Queue.QueueManager;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class CountRegistrationsCollector extends Thread implements CollectorInterface, ConsumerInterface
{
    public final static  String COLLECTOR_NAME               = "CountRegistrations";
    private final static long   PERIOD_REGISTRATIONS_MINUTES = 360;

    private final QueueManager queueManager;

    private final AtomicInteger userRegisteredInPeriod;

    private final LoggerInterface logger;

    private final AtomicInteger currentlyRegisteredUsers;

    private boolean running = true;

    public CountRegistrationsCollector()
    {
        UserRepository userRepository = (UserRepository) ObjectManager.get(UserRepository.class);

        this.queueManager             = (QueueManager) ObjectManager.get(QueueManager.class);
        this.userRegisteredInPeriod   = new AtomicInteger();
        this.currentlyRegisteredUsers = new AtomicInteger(userRepository.getUserQuantity());
        this.logger                   = (LoggerInterface) ObjectManager.get(LoggerInterface.class);
    }

    @Override
    public void run()
    {
        ScheduledFuture<?> loadAmountOfRegisteredUsersInPeriodTask = this
                .createScheduledExecutorService()
                .scheduleAtFixedRate(this::registeredUsersInPeriod, 0, 10, TimeUnit.MINUTES);

        while (this.running) {
            try {
                this.queueManager.take(RegistrationPublisher.QUEUE_ID);
                this.currentlyRegisteredUsers.incrementAndGet();
            } catch (Throwable e) {
                this.logger.logException("Something really weird happened, and this crashed: " + e.getMessage(), e);
            }
        }

        loadAmountOfRegisteredUsersInPeriodTask.cancel(true);
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
            put("Currently Registered User", currentlyRegisteredUsers.get());
            put("Registrations in Period", userRegisteredInPeriod.get());
        }};
    }

    @Override
    public void stopRun()
    {
        this.running = false;
    }

    private void registeredUsersInPeriod()
    {
        try {
            UserSearchCriteria searchCriteria = (UserSearchCriteria) ObjectManager.create(UserSearchCriteria.class);
            searchCriteria.createdAtSinceMinutes = Math.toIntExact(PERIOD_REGISTRATIONS_MINUTES);

            this.userRegisteredInPeriod.set(this.getUserRepository().getUserQuantityBySearchCriteria(searchCriteria));
        } catch (Throwable e) {
            this.logger.logException("[ERROR] Load Amount of new registered users failed: " + e.getMessage(), e);
        }
    }

    private UserRepository getUserRepository()
    {
        return (UserRepository) ObjectManager.get(UserRepository.class);
    }

    private ScheduledExecutorService createScheduledExecutorService()
    {
        return (ScheduledExecutorService) ObjectManager.create(ScheduledExecutorService.class);
    }
}
