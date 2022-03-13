package hoot.front.Service;

import hoot.model.entities.History;
import hoot.model.entities.Tag;
import hoot.model.monitoring.CacheSizeCollector;
import hoot.model.monitoring.QueueSizeCollector;
import hoot.model.monitoring.SystemWorkloadCollector;
import hoot.model.monitoring.TagCollector;
import hoot.model.monitoring.consumer.CountLoginsCollector;
import hoot.model.monitoring.consumer.CountRegistrationsCollector;
import hoot.model.monitoring.consumer.RequestDurationCollector;
import hoot.model.monitoring.consumer.RequestsCollector;
import hoot.model.repositories.HistoryRepository;
import hoot.system.Exception.CouldNotSaveException;
import hoot.system.Logger.LoggerInterface;
import hoot.system.Monitoring.CollectorResult;
import hoot.system.Monitoring.Monitor;
import hoot.system.Monitoring.MonitorData;
import hoot.system.ObjectManager.ObjectManager;
import hoot.system.Serializer.Serializer;
import hoot.system.Service.ServiceInterface;

import java.util.ArrayList;
import java.util.Map;

public class HistoryService implements ServiceInterface
{
    private final Monitor monitor;

    private final HistoryRepository historyRepository;

    private final Serializer serializer;

    private final LoggerInterface logger;

    public HistoryService()
    {
        this.monitor           = (Monitor) ObjectManager.get(Monitor.class);
        this.historyRepository = (HistoryRepository) ObjectManager.get(HistoryRepository.class);
        this.serializer        = (Serializer) ObjectManager.get(Serializer.class);
        this.logger            = (LoggerInterface) ObjectManager.get(LoggerInterface.class);
    }

    @Override
    public void execute()
    {
        try {
            History     entity      = (History) ObjectManager.create(History.class);
            MonitorData monitorData = this.monitor.getMonitorData();

            CollectorResult logins         = monitorData.get(CountLoginsCollector.COLLECTOR_NAME);
            CollectorResult registrations  = monitorData.get(CountRegistrationsCollector.COLLECTOR_NAME);
            CollectorResult workload       = monitorData.get(SystemWorkloadCollector.COLLECTOR_NAME);
            CollectorResult requests       = monitorData.get(RequestsCollector.COLLECTOR_NAME);
            CollectorResult avgReqDuration = monitorData.get(RequestDurationCollector.COLLECTOR_NAME);
            CollectorResult mostUsedTags   = monitorData.get(TagCollector.COLLECTOR_NAME);
            CollectorResult queueSizes     = monitorData.get(QueueSizeCollector.COLLECTOR_NAME);
            CollectorResult cacheSizes     = monitorData.get(CacheSizeCollector.COLLECTOR_NAME);

            entity.loginsPerSixHours        = (Integer) logins.get("LoginsPerPeriod");
            entity.currentlyRegisteredUsers = (Integer) registrations.get("Currently Registered User");
            entity.registrationsPerSixHours = (Integer) registrations.get("Registrations in Period");
            entity.currentLoggedIn          = (Integer) requests.get("Currently Logged In");
            //entity.requestsLoggedInPerSecond = requests.get("Requests Logged In Per Second");
            entity.requestsPerSecond = (Integer) requests.get("Requests Per Second");
            entity.queueSize         = queueSizes;
            entity.cacheSize         = cacheSizes;
            entity.workload          = workload;
            entity.threadCount       = (Integer) workload.get("Thread Count");
            entity.threadCountTotal  = (Long) workload.get("Thread Total Started Count");
            entity.systemLoadAverage = (Double) workload.get("System Load Average");
            entity.systemCPULoad     = (Double) workload.get("System CPU Load");
            entity.processCPULoad    = (Double) workload.get("Process CPU Load");

            entity.trendingHashtags = (ArrayList<Tag>) mostUsedTags.get("popularTags");

            //System.gc();

            try {
                historyRepository.save(entity);
            } catch (CouldNotSaveException e) {
                this.logger.log("[ERROR] Could not save history entry: " + e.getMessage());
            }
        } catch (Exception e) {
            String msg = "[ERROR] Could not execute HistoryService::execute properly: " + e.getMessage();
            this.logger.logException(msg, e);
        }
    }

    private void mapLoginsToEntity(Map<String, Object> loginData, History entity)
    {
        if (loginData == null) {
            return;
        }

        entity.currentLoggedIn = (Integer) loginData.get("CurrentlyLoggedIn");
    }

    private void mapRegistrationsToEntity(Map<String, Object> registrationData, History entity)
    {
        if (registrationData == null) {
            return;
        }

        entity.currentlyRegisteredUsers = (Integer) registrationData.get("CurrentlyRegisteredUser");
    }
}
