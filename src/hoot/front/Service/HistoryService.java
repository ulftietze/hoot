package hoot.front.Service;

import hoot.model.entities.Historie;
import hoot.model.entities.Tag;
import hoot.model.monitoring.SystemWorkloadCollector;
import hoot.model.monitoring.consumer.CountLoginsCollector;
import hoot.model.monitoring.consumer.CountRegistrationsCollector;
import hoot.model.monitoring.TagCollector;
import hoot.model.monitoring.consumer.RequestsCollector;
import hoot.model.repositories.HistorieRepository;
import hoot.system.Logger.LoggerInterface;
import hoot.system.Monitoring.CollectorResult;
import hoot.system.Monitoring.Monitor;
import hoot.system.Monitoring.MonitorData;
import hoot.system.ObjectManager.ObjectManager;
import hoot.system.Service.ServiceInterface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class HistoryService implements ServiceInterface
{
    private final Monitor monitor;

    private final HistorieRepository historyRepository;

    private final LoggerInterface logger;

    public HistoryService()
    {
        this.monitor           = (Monitor) ObjectManager.get(Monitor.class);
        this.historyRepository = (HistorieRepository) ObjectManager.get(HistorieRepository.class);
        this.logger            = (LoggerInterface) ObjectManager.get(LoggerInterface.class);
    }

    @Override
    public void execute()
    {
        try {
            Historie    entity      = (Historie) ObjectManager.create(Historie.class);
            MonitorData monitorData = this.monitor.getMonitorData();

            CollectorResult logins        = monitorData.get(CountLoginsCollector.COLLECTOR_NAME);
            CollectorResult registrations = monitorData.get(CountRegistrationsCollector.COLLECTOR_NAME);
            CollectorResult workload      = monitorData.get(SystemWorkloadCollector.COLLECTOR_NAME);
            CollectorResult requests      = monitorData.get(RequestsCollector.COLLECTOR_NAME);
            CollectorResult mostUsedTags  = monitorData.get(TagCollector.COLLECTOR_NAME);

            this.logger.log("\n"
                            + "LoginsPerPeriod: " + logins.get("LoginsPerPeriod") + "\n"
                            + "Currently Registered User: " + registrations.get("Currently Registered User") + "\n"
                            + "Registrations in Period: " + registrations.get("Registrations in Period") + "\n"
                            + "Currently Logged In: " + requests.get("Currently Logged In") + "\n"
                            + "Requests Per Second: " + requests.get("Requests Per Second") + "\n"
                            + "Memory used: " + workload.get("Memory used") + "\n"
                            + "Available Processors: " + workload.get("Available Processors") + "\n"
                            + "System Load Average: " + workload.get("System Load Average") + "\n"
                            + "Process CPU Load: " + workload.get("Process CPU Load") + "\n"
                            + "Most recent tags: "
                            + ((ArrayList<Tag>) mostUsedTags.get("popularTags")).stream().map(t -> t.tag).collect(Collectors.joining(",")) + "\n"
            );

            //try {
            //    historyRepository.save(entity);
            //} catch (CouldNotSaveException e) {
            //    this.logger.log("[ERROR] Could not save history entry: " + e.getMessage());
            //}
        } catch (Exception e) {
            String msg = "[ERROR] Could not execute HistoryService::execute properly: " + e.getMessage();
            this.logger.logException(msg, e);
        }
    }

    private void mapLoginsToEntity(Map<String, Object> loginData, Historie entity)
    {
        if (loginData == null) {
            return;
        }

        entity.currentLoggedIn = (Integer) loginData.get("CurrentlyLoggedIn");
    }

    private void mapRegistrationsToEntity(Map<String, Object> registrationData, Historie entity)
    {
        if (registrationData == null) {
            return;
        }

        entity.currentlyRegisteredUsers = (Integer) registrationData.get("CurrentlyRegisteredUser");
    }
}