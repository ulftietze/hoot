package hoot.front.Service;

import hoot.model.entities.Historie;
import hoot.model.monitoring.SystemWorkloadCollector;
import hoot.model.monitoring.consumer.CountLoginsCollector;
import hoot.model.monitoring.consumer.CountRegistrationsCollector;
import hoot.model.repositories.HistorieRepository;
import hoot.system.Logger.LoggerInterface;
import hoot.system.Monitoring.CollectorResult;
import hoot.system.Monitoring.Monitor;
import hoot.system.Monitoring.MonitorData;
import hoot.system.ObjectManager.ObjectManager;
import hoot.system.Service.ServiceInterface;

import java.util.Arrays;
import java.util.Map;

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

            this.mapLoginsToEntity(logins, entity);
            this.mapRegistrationsToEntity(registrations, entity);

            this.logger.log("\n"
                            + entity.currentLoggedIn + "\n"
                            + entity.currentlyRegisteredUsers + "\n"
                            + workload.get("Memory used") + "\n"
            );

            //try {
            //    historyRepository.save(entity);
            //} catch (CouldNotSaveException e) {
            //    this.logger.log("[ERROR] Could not save history entry: " + e.getMessage());
            //}
        } catch (Exception e) {
            String msg = "[ERROR] Could not execute HistoryService::execute properly: " + e.getMessage();
            this.logger.log(msg + "\n" + Arrays.toString(e.getStackTrace()));
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
