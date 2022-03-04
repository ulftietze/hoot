package hoot.front.Service;

import hoot.model.entities.Historie;
import hoot.model.repositories.HistorieRepository;
import hoot.system.Exception.CouldNotSaveException;
import hoot.system.Logger.LoggerInterface;
import hoot.system.Monitoring.Monitor;
import hoot.system.Monitoring.MonitorData;
import hoot.system.ObjectManager.ObjectManager;
import hoot.system.Service.ServiceInterface;

import java.util.HashMap;

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
        Historie    entity      = (Historie) ObjectManager.create(Historie.class);
        MonitorData monitorData = this.monitor.getMonitorData();

        try {
            historyRepository.save(entity);
        } catch (CouldNotSaveException e) {
            this.logger.log("[ERROR] Could not save history entry: " + e.getMessage());
        }
    }

    private void mapLoginsToEntity(HashMap<String, Object> loginData, Historie entity)
    {
        entity.currentLoggedIn = (Integer) loginData.get("CurrentlyLoggedIn");
    }

    private void mapRegistrationsToEntity(HashMap<String, Object> loginData, Historie entity)
    {
        entity.currentlyRegisteredUsers = (Integer) loginData.get("RegistrationsPerPeriod");
    }
}
