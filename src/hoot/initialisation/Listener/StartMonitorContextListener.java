package hoot.initialisation.Listener;

import hoot.front.Service.HistoryService;
import hoot.model.monitoring.QueueSizeCollector;
import hoot.model.monitoring.SystemWorkloadCollector;
import hoot.model.monitoring.TagCollector;
import hoot.model.monitoring.consumer.CountLoginsCollector;
import hoot.model.monitoring.consumer.CountRegistrationsCollector;
import hoot.model.monitoring.consumer.RequestsCollector;
import hoot.system.Monitoring.Monitor;
import hoot.system.ObjectManager.ObjectManager;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@WebListener
public class StartMonitorContextListener implements ServletContextListener
{
    private ScheduledFuture<?> historyScheduleAtFixedRate;

    private boolean initialized = false;

    public void contextInitialized(ServletContextEvent contextEvent)
    {
        contextEvent.getServletContext().log("init StartMonitorContextListener");

        // Get Collectors
        CountLoginsCollector        loginsCollector         = this.getLoginsCollector();
        CountRegistrationsCollector registrationsCollector  = this.getRegistrationsCollector();
        SystemWorkloadCollector     systemWorkloadCollector = this.getSystemWorkloadCollector();
        TagCollector                tagCollector            = this.getHashtagCollector();
        RequestsCollector           requestsCollector       = this.getRequestCollector();
        QueueSizeCollector          queueSizeCollector      = this.getQueueSizeCollector();

        // Start Collector when a thread
        loginsCollector.start();
        registrationsCollector.start();
        tagCollector.start();
        requestsCollector.start();

        // Register Collectors in Monitor and start monitoring
        Monitor monitor = (Monitor) ObjectManager.get(Monitor.class);
        monitor.addCollector(loginsCollector);
        monitor.addCollector(registrationsCollector);
        monitor.addCollector(requestsCollector);
        monitor.addCollector(tagCollector);
        monitor.addCollector(systemWorkloadCollector);
        monitor.addCollector(queueSizeCollector);
        monitor.start();

        // Initialize recurring task to collect monitor data
        HistoryService historyService = (HistoryService) ObjectManager.get(HistoryService.class);
        this.historyScheduleAtFixedRate = this
                .createScheduledExecutorService()
                .scheduleAtFixedRate(historyService::execute, 10, 1, TimeUnit.SECONDS);

        this.initialized = true;
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce)
    {
        if (!initialized) {
            return;
        }

        this.historyScheduleAtFixedRate.cancel(true);

        // Get Collectors
        CountLoginsCollector        loginsCollector        = this.getLoginsCollector();
        CountRegistrationsCollector registrationsCollector = this.getRegistrationsCollector();
        TagCollector                tagCollector           = this.getHashtagCollector();
        RequestsCollector           requestsCollector      = this.getRequestCollector();
        Monitor                     monitor                = (Monitor) ObjectManager.get(Monitor.class);

        requestsCollector.stopRun();
        loginsCollector.stopRun();
        registrationsCollector.stopRun();
        tagCollector.stopRun();
        monitor.stopRun();

        requestsCollector.interrupt();
        loginsCollector.interrupt();
        registrationsCollector.interrupt();
        tagCollector.interrupt();
        monitor.interrupt();

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            sce.getServletContext().log("Could not wait: " + e.getMessage());
        }
    }

    private TagCollector getHashtagCollector()
    {
        return (TagCollector) ObjectManager.get(TagCollector.class);
    }

    private CountLoginsCollector getLoginsCollector()
    {
        return (CountLoginsCollector) ObjectManager.get(CountLoginsCollector.class);
    }

    private CountRegistrationsCollector getRegistrationsCollector()
    {
        return (CountRegistrationsCollector) ObjectManager.get(CountRegistrationsCollector.class);
    }

    private SystemWorkloadCollector getSystemWorkloadCollector()
    {
        return (SystemWorkloadCollector) ObjectManager.get(SystemWorkloadCollector.class);
    }

    private RequestsCollector getRequestCollector()
    {
        return (RequestsCollector) ObjectManager.get(RequestsCollector.class);
    }

    private ScheduledExecutorService createScheduledExecutorService()
    {
        return (ScheduledExecutorService) ObjectManager.create(ScheduledExecutorService.class);
    }

    private QueueSizeCollector getQueueSizeCollector()
    {
        return (QueueSizeCollector) ObjectManager.get(QueueSizeCollector.class);
    }
}
