package hoot.initialisation.Listener;

import hoot.front.Service.HistoryService;
import hoot.model.monitoring.SystemWorkloadCollector;
import hoot.model.monitoring.consumer.CountLoginsCollector;
import hoot.model.monitoring.consumer.CountRegistrationsCollector;
import hoot.system.Monitoring.Monitor;
import hoot.system.ObjectManager.ObjectManager;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@WebListener
public class StartMonitorContextListener implements ServletContextListener
{
    public void contextInitialized(ServletContextEvent contextEvent)
    {
        contextEvent.getServletContext().log("init StartMonitorContextListener");

        // Get Collectors
        CountLoginsCollector        loginsCollector         = this.getLoginsCollector();
        CountRegistrationsCollector registrationsCollector  = this.getRegistrationsCollector();
        SystemWorkloadCollector     systemWorkloadCollector = this.getSystemWorkloadCollector();

        // Start Collector when a thread
        loginsCollector.start();
        registrationsCollector.start();

        // Register Collectors in Monitor and start monitoring
        Monitor monitor = (Monitor) ObjectManager.get(Monitor.class);
        monitor.addCollector(loginsCollector);
        monitor.addCollector(registrationsCollector);
        monitor.addCollector(systemWorkloadCollector);
        monitor.start();

        // Initialize recurring task to collect monitor data
        HistoryService historyService = (HistoryService) ObjectManager.get(HistoryService.class);
        this.createScheduledExecutorService().scheduleAtFixedRate(historyService::execute, 10, 10, TimeUnit.SECONDS);
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
