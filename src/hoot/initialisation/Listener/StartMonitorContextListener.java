package hoot.initialisation.Listener;

import hoot.front.Service.HistoryService;
import hoot.model.monitoring.SystemWorkloadCollector;
import hoot.model.monitoring.consumer.CountLoginsCollector;
import hoot.model.monitoring.consumer.CountRegistrationsCollector;
import hoot.model.monitoring.consumer.TagCollector;
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
    private ScheduledFuture<?> scheduleAtFixedRate;

    public void contextInitialized(ServletContextEvent contextEvent)
    {
        contextEvent.getServletContext().log("init StartMonitorContextListener");

        // Get Collectors
        CountLoginsCollector        loginsCollector         = this.getLoginsCollector();
        CountRegistrationsCollector registrationsCollector  = this.getRegistrationsCollector();
        SystemWorkloadCollector     systemWorkloadCollector = this.getSystemWorkloadCollector();
        TagCollector                tagCollector            = this.getHashtagCollector();

        // Start Collector when a thread
        loginsCollector.start();
        registrationsCollector.start();
        tagCollector.start();

        // Register Collectors in Monitor and start monitoring
        Monitor monitor = (Monitor) ObjectManager.get(Monitor.class);
        monitor.addCollector(loginsCollector);
        monitor.addCollector(registrationsCollector);
        monitor.addCollector(systemWorkloadCollector);
        monitor.addCollector(tagCollector);
        monitor.start();

        // Initialize recurring task to collect monitor data
        HistoryService historyService = (HistoryService) ObjectManager.get(HistoryService.class);
        this.scheduleAtFixedRate = this
                .createScheduledExecutorService()
                .scheduleAtFixedRate(historyService::execute, 10, 10, TimeUnit.SECONDS);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce)
    {
        this.scheduleAtFixedRate.cancel(true);

        // Get Collectors
        CountLoginsCollector        loginsCollector        = this.getLoginsCollector();
        CountRegistrationsCollector registrationsCollector = this.getRegistrationsCollector();
        TagCollector                tagCollector           = this.getHashtagCollector();
        Monitor                     monitor                = (Monitor) ObjectManager.get(Monitor.class);

        monitor.stopRun();
        loginsCollector.stopRun();
        registrationsCollector.stopRun();
        tagCollector.stopRun();

        monitor.interrupt();
        loginsCollector.interrupt();
        registrationsCollector.interrupt();
        tagCollector.interrupt();

        try {
            Thread.sleep(2000L);
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

    private ScheduledExecutorService createScheduledExecutorService()
    {
        return (ScheduledExecutorService) ObjectManager.create(ScheduledExecutorService.class);
    }
}
